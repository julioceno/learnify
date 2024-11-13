package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.dto.UserQueueDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// TODO: remover prefixo handle
@Service
@Slf4j
public class HandleReturnSignatureService {
    @Value("${aws.services.queue.url.cancel-subscription}")
    private String cancelSubscriptionUrl;

    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;

    public HandleReturnSignatureService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
    }

    public void run(MessageQueueDTO<UserQueueDTO> messageQueueDTO) {
        log.info("Received message for user {}", messageQueueDTO.data().userId());

        if (messageQueueDTO.ok()) {
            log.info("Subscription is successfully, removing idempotency id...");
            idempotencyService.remove(messageQueueDTO.data().userId());
            log.info("Idempotency id removed");
            return;
        }

        log.info("Subscription is fail, call payment ms for reset operation");
        publishMessageQueueService.run(cancelSubscriptionUrl, messageQueueDTO);
    }
}

package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleReturnSignatureService {
    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    @Autowired
    private IdempotencyService idempotencyService;

    public void run(MessageQueueDTO messageQueueDTO) {
        log.info("Received message for user {}", messageQueueDTO.userId());

        if (messageQueueDTO.ok()) {
            log.info("Subscription is successfully, removing idempotency id...");
            idempotencyService.remove(messageQueueDTO.userId());
            log.info("Idempotency id removed");
            return;
        }

        log.info("Subscription is fail, call payment ms for reset operation");
        publishMessageQueueService.run("", messageQueueDTO);
    }
}

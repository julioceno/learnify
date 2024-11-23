package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.ReturnSignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// TODO: remover prefixo handle
@Service
@Slf4j
public class HandleReturnSignatureService {
    @Value("${aws.services.queue.url.cancel-signature}")
    private String cancelSignatureUrl;

    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;

    public HandleReturnSignatureService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
    }

    public void run(MessageQueueDTO<ReturnSignatureDTO> dto) {
        log.info("Received message for user {}", dto.data().userId());

        if (dto.ok()) {
            log.info("Signature is successfully, removing idempotency id...");
            idempotencyService.remove(dto.data().userId());
            log.info("Idempotency id removed");
            return;
        }

        log.info("Signature is fail, call payment ms for reset operation");
        publishMessageQueueService.run(cancelSignatureUrl, dto);
    }
}

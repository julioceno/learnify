package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.ReturnPaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleReturnPaymentService {
    @Value("${aws.services.queue.url.signature}")
    private String signatureUrl;

    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;

    public HandleReturnPaymentService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
    }

    public void run(MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO) {
        log.info("Received message for user {}", messageQueueDTO.data().userId());

        if (messageQueueDTO.ok()) {
            log.info("Payment is successfully, call signature service");
            publishMessageQueueService.run(signatureUrl, messageQueueDTO);
            return;
        }

        // TODO: fazer alguma tratativa para mostrar que o pagamento não deu certo
        idempotencyService.remove(messageQueueDTO.data().userId());
    }
}

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
public class HandleReturnCancelSignatureService {
    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;

    public HandleReturnCancelSignatureService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
    }

    public void run(MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO) {
        log.info("Received message for user {}", messageQueueDTO.data().userId());
        // TODO: adicionar no banco uma mensagem que o cancelamento da assinatura foi mal sucedido e fara mais tentativas

        // TODO: fazer alguma tratativa para mostrar que o pagamento não deu certo
        idempotencyService.remove(messageQueueDTO.data().userId());
    }
}

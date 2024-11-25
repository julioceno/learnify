package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.DataDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.CreateSubscriptionDTO;
import com.learnify.order.order.dto.ReturnPaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// TODO: revisar logs
@Service
@Slf4j
public class HandleReturnPaymentService {
    @Value("${api.order.idempotency.time}")
    private int idempotencyTime;

    @Value("${aws.services.queue.url.signature}")
    private String signatureUrl;

    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;

    public HandleReturnPaymentService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
    }

    public void run(MessageQueueDTO<ReturnPaymentDTO> dto) {
        String userId = dto.data().userId();
        log.info("Received message for user {}", userId);

        if (dto.ok()) {
            DataDTO dataDTO = updateIdempotency(dto.data());
            publishMessageSignature(userId, dataDTO);
            return;
        }

        // TODO: fazer alguma tratativa para mostrar que o pagamento não deu certo
        idempotencyService.remove(dto.data().userId());
    }

    private DataDTO updateIdempotency(ReturnPaymentDTO dto) {
        log.info("Getting idempotence value by key {}...", dto.userId());
        DataDTO dataDTO = idempotencyService.get(dto.userId());

        log.info("Value obtained, set subscriptionId into data to update...");
        dataDTO.setSubscriptionId(dto.subscriptionId());
        idempotencyService.update(dto.userId(), dataDTO, idempotencyTime);
        log.info("Update info");
        return dataDTO;
    }

    private void publishMessageSignature(String userId, DataDTO dataDTO) {
        log.info("Payment is successfully, call signature service");
        CreateSubscriptionDTO createSubscriptionDTO = new CreateSubscriptionDTO(userId, dataDTO.getPlanId());
        MessageQueueDTO<CreateSubscriptionDTO> messageQueueDTO = new MessageQueueDTO<CreateSubscriptionDTO>(true, createSubscriptionDTO);

        publishMessageQueueService.run(signatureUrl, messageQueueDTO);
    }
}

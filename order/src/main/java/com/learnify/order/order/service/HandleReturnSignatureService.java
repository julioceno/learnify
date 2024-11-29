package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.DataDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.domain.Order;
import com.learnify.order.order.domain.OrderRepository;
import com.learnify.order.order.domain.StatusOrder;
import com.learnify.order.order.dto.CancelSignatureDTO;
import com.learnify.order.order.dto.ReturnSignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleReturnSignatureService {
    @Value("${aws.services.queue.url.cancel-signature}")
    private String cancelSignatureUrl;

    private final PublishMessageQueueService publishMessageQueueService;
    private final IdempotencyService idempotencyService;
    private final OrderRepository orderRepository;

    public HandleReturnSignatureService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService, OrderRepository orderRepository) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
        this.orderRepository = orderRepository;
    }

    public void run(MessageQueueDTO<ReturnSignatureDTO> dto) {
        String userId = dto.data().userId();
        log.info("Received message for user {}", userId);

        if (dto.ok()) {
            updateOrder(dto.data());
            removeIdempotenceKey(userId);
            return;
        }

        log.info("Signature is fail, call payment ms for reset operation");
        DataDTO dataDTO = getData(dto.data().userId());
        publishMessageToCancelMessage(dto.data(), dataDTO);
    }

    private void updateOrder(ReturnSignatureDTO signatureDTO) {
        log.info("Finding order with error status...");
        Order order = orderRepository.findOneById(signatureDTO.orderId()).get();

        log.info("Updating order...");
        order.setStatus(StatusOrder.SUCCESSFULY);
        order.setSignatureId(""); // TODO: retornar o valor correto

        orderRepository.save(order);

    }

    private void removeIdempotenceKey(String userId) {
        log.info("Signature is successfully, removing idempotency id...");
        idempotencyService.remove(userId);
        log.info("Idempotency id removed");
    }

    private DataDTO getData(String userId) {
        // TODO: caso não existir, pegar o dado do banco de dados

        log.info("Getting idempotency data...");
        DataDTO dataDTO = idempotencyService.get(userId);
        log.info("Data obtained");
        return dataDTO;
    }

    private void publishMessageToCancelMessage(ReturnSignatureDTO dto, DataDTO dataDTO) {
        log.info("Creating dto to sending...");
        CancelSignatureDTO cancelSignatureDTO = new CancelSignatureDTO(dto.orderId(), dto.userId(), dataDTO.getSubscriptionId());
        MessageQueueDTO<CancelSignatureDTO> messageQueueDTO = new MessageQueueDTO<CancelSignatureDTO>(true, cancelSignatureDTO);

        log.info("Sending message...");
        publishMessageQueueService.run(cancelSignatureUrl, messageQueueDTO);
        log.info("Message sent");
    }
}

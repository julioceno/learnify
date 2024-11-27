package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.DataDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.domain.Order;
import com.learnify.order.order.domain.OrderRepository;
import com.learnify.order.order.domain.StatusOrder;
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
    private final OrderRepository orderRepository;

    public HandleReturnPaymentService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService, OrderRepository orderRepository) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
        this.orderRepository = orderRepository;
    }

    public void run(MessageQueueDTO<ReturnPaymentDTO> dto) {
        String userId = dto.data().userId();
        log.info("Received message for user {}", userId);

        if (dto.ok()) {
            DataDTO dataDTO = updateIdempotency(dto.data());
            updateOrderSuccessfully(dto.data());
            publishMessageSignature(dto.data(), dataDTO);
            return;
        }

        updateOrderError(dto.data());
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

    private void updateOrderSuccessfully(ReturnPaymentDTO dto) {
        log.info("Finding order...");
        Order order = orderRepository.findOneById(dto.orderId()).get();

        log.info("Updating order...");
        order.setSubscriptionId(dto.subscriptionId());

        orderRepository.save(order);
        log.info("Updated order");
    }

    private void updateOrderError(ReturnPaymentDTO dto) {
        log.info("Finding order with error status...");

        Order order = orderRepository.findOneById(dto.orderId()).get();

        log.info("Updating order...");
        order.setSubscriptionId(dto.subscriptionId());
        order.setMessageError("Ocorreu um erro ao tentar criar a assinatura");
        order.setStatus(StatusOrder.ERROR);

        orderRepository.save(order);
        log.info("Updated order");
    }

    private void publishMessageSignature(ReturnPaymentDTO dto, DataDTO dataDTO) {
        log.info("Payment is successfully, call signature service");
        CreateSubscriptionDTO createSubscriptionDTO = new CreateSubscriptionDTO(dto.orderId(), dto.userId(), dataDTO.getPlanId());
        MessageQueueDTO<CreateSubscriptionDTO> messageQueueDTO = new MessageQueueDTO<CreateSubscriptionDTO>(true, createSubscriptionDTO);

        publishMessageQueueService.run(signatureUrl, messageQueueDTO);
    }
}

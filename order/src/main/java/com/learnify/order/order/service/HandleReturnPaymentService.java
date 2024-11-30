package com.learnify.order.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.dto.ReturnErrorDTO;
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
    private final ObjectMapper objectMapper;

    public HandleReturnPaymentService(PublishMessageQueueService publishMessageQueueService, IdempotencyService idempotencyService, OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.idempotencyService = idempotencyService;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    public void run(MessageQueueDTO<?> dto) {

        if (dto.ok()) {
            ReturnPaymentDTO returnPaymentDTO = objectMapper.convertValue(dto.data(), ReturnPaymentDTO.class);
            createPayment(returnPaymentDTO);
            return;
        }
        ReturnErrorDTO returnErrorDTO = objectMapper.convertValue(dto.data(), ReturnErrorDTO.class);
        updateOrderError(returnErrorDTO);
        log.info("Finished");
    }

    private void createPayment(ReturnPaymentDTO dto) {
        String userId = dto.userId();
        log.info("Received message for user {}", userId);

        DataDTO dataDTO = updateIdempotency(dto);
        updateOrderSuccessfully(dto);
        publishMessageSignature(dto, dataDTO);
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

    private void updateOrderError(ReturnErrorDTO dto) {
        log.info("Finding order with error status...");
        Order order = orderRepository.findOneById(dto.orderId()).get();

        log.info("Updating order...");
        order.setMessageError("Ocorreu um erro ao tentar criar a assinatura");
        order.setStatus(StatusOrder.ERROR);
        order.setMessageError(dto.message());
        orderRepository.save(order);

        log.info("Updated order, revoke idempotency...");
        idempotencyService.remove(dto.userId());
    }

    private void publishMessageSignature(ReturnPaymentDTO dto, DataDTO dataDTO) {
        log.info("Payment is successfully, call signature service");
        CreateSubscriptionDTO createSubscriptionDTO = new CreateSubscriptionDTO(dto.orderId(), dto.userId(), dataDTO.getPlanId());
        MessageQueueDTO<CreateSubscriptionDTO> messageQueueDTO = new MessageQueueDTO<CreateSubscriptionDTO>(true, createSubscriptionDTO);

        publishMessageQueueService.run(signatureUrl, messageQueueDTO);
    }
}

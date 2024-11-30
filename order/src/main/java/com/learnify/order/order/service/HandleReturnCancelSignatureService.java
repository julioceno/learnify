package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.order.domain.Order;
import com.learnify.order.order.domain.OrderRepository;
import com.learnify.order.order.domain.StatusOrder;
import com.learnify.order.order.dto.ReturnPaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleReturnCancelSignatureService {
    private final IdempotencyService idempotencyService;
    private final OrderRepository orderRepository;

    public HandleReturnCancelSignatureService(IdempotencyService idempotencyService, OrderRepository orderRepository) {
        this.idempotencyService = idempotencyService;
        this.orderRepository = orderRepository;
    }

    public void run(MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO) {
        log.info("Received message for user {}", messageQueueDTO.data().userId());
        updateOrderError(messageQueueDTO.data().orderId());

        log.info("revoking idempotency...");
        idempotencyService.remove(messageQueueDTO.data().userId());
        log.info("Revoked");
    }

    private void updateOrderError(String orderId) {
        log.info("Finding order with error status...");
        Order order = orderRepository.findOneById(orderId).get();

        log.info("Updating order...");
        order.setMessageError("Ocorreu um erro ao tentar criar a assinatura, foi feito o cancelamento da assinatura");
        order.setStatus(StatusOrder.ERROR);
        orderRepository.save(order);

        log.info("Updated order");
    }
}

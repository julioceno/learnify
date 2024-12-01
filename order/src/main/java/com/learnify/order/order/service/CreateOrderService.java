package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.common.exception.BadRequestException;
import com.learnify.order.common.dto.IdempotencyDataDTO;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.domain.Order;
import com.learnify.order.order.domain.OrderRepository;
import com.learnify.order.order.domain.StatusOrder;
import com.learnify.order.order.dto.CreateOrderDTO;
import com.learnify.order.order.dto.payment.CustomerDTO;
import com.learnify.order.order.dto.payment.PlanDTO;
import com.learnify.order.order.dto.payment.SignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateOrderService {
    @Value("${api.order.idempotency.time}")
    private int idempotencyTime;

    @Value("${aws.services.queue.url.payment}")
    private String paymentUrl;

    private final IdempotencyService idempotencyService;
    private final PublishMessageQueueService publishMessageQueueService;
    private final GetPlanService getPlanService;
    private final OrderRepository orderRepository;

    public CreateOrderService(IdempotencyService idempotencyService, PublishMessageQueueService publishMessageQueueService, GetPlanService getPlanService, OrderRepository orderRepository) {
        this.idempotencyService = idempotencyService;
        this.publishMessageQueueService = publishMessageQueueService;
        this.getPlanService = getPlanService;
        this.orderRepository = orderRepository;
    }

    public void run(UserDTO user, CreateOrderDTO createOrderDTO) {
        createIdempotencyId(user.getId(), createOrderDTO);
        com.learnify.order.order.dto.plan.PlanDTO planDTO = getPlanService.run(createOrderDTO.planId());
        Order order = createOrder(planDTO.getId(), user.getId());
        try {
            SignatureDTO signatureDTO = createSignatureDTO(order.getId(), user, planDTO);
            MessageQueueDTO<SignatureDTO> messageQueueDTO = new MessageQueueDTO<SignatureDTO>(true, signatureDTO);

            publishMessageQueueService.run(paymentUrl, messageQueueDTO);
        } catch (RuntimeException e) {
            updateOrderError(order.getId());
            revokeIdempotencyId(user.getId());
            throw e;
        }
    }

    private void createIdempotencyId(String key, CreateOrderDTO createOrderDTO) {
        log.info("Generate idempotency id...");
        final IdempotencyDataDTO dataDTO = new IdempotencyDataDTO(createOrderDTO.planId(), null);
        boolean isSuccess = idempotencyService.create(key, dataDTO, idempotencyTime);

        if (!isSuccess) {
            log.error("Ocurred error when try generate idempotency id");
            throw new BadRequestException("Ocorreu um erro ao tentar efeturar a assinatura. Tente novamente mais tarde");
        }

        log.info("Idempotency id created");
    }

    private SignatureDTO createSignatureDTO(String orderId, UserDTO user, com.learnify.order.order.dto.plan.PlanDTO planDTO) {
        log.info("Creating signature dto...");
        CustomerDTO customer = new CustomerDTO(user.getId(), user.getCustomerId());
        PlanDTO planData = new PlanDTO(
                planDTO.getId(),
                planDTO.getStripeId(),
                planDTO.getValue()
        );

        SignatureDTO signatureDTO = new SignatureDTO(orderId, customer, planData);

        log.info("Signature dto created");
        return signatureDTO;
    }

    private Order createOrder(String planId, String userId) {
        log.info("Creating order in database...");
        Order order = new Order();
        order.setStatus(StatusOrder.PROCESSING);
        order.setPlanId(planId);
        order.setUserId(userId);

        Order orderCreated = orderRepository.insert(order);
        log.info("Order created");
        return orderCreated;
    }


    private void updateOrderError(String orderId) {
        log.info("Finding order with error status...");
        Order order = orderRepository.findOneById(orderId).get();

        log.info("Updating order...");
        order.setMessageError("Ocorreu um erro ao tentar criar a assinatura");
        order.setStatus(StatusOrder.ERROR);
        orderRepository.save(order);

        log.info("Order updated");
    }

    private void revokeIdempotencyId(String key) {
        log.info("Deleting idempotency id...");
        idempotencyService.remove(key);
        log.info("Id deleted");
    }
}

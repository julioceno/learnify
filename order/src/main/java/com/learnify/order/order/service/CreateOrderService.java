package com.learnify.order.order.service;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.common.exception.BadRequestException;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.CreateOrderDTO;
import com.learnify.order.order.dto.payment.CardDTO;
import com.learnify.order.order.dto.payment.CustomerDTO;
import com.learnify.order.order.dto.payment.PlanDTO;
import com.learnify.order.order.dto.payment.SignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    public CreateOrderService(IdempotencyService idempotencyService, PublishMessageQueueService publishMessageQueueService, GetPlanService getPlanService) {
        this.idempotencyService = idempotencyService;
        this.publishMessageQueueService = publishMessageQueueService;
        this.getPlanService = getPlanService;
    }

    public void run(UserDTO user, CreateOrderDTO createOrderDTO) {
        createIdempotencyId(user.getId());

        try {
            com.learnify.order.order.dto.plan.PlanDTO planDTO = getPlanService.run(createOrderDTO.planId());
            SignatureDTO signatureDTO = createSignatureDTO(user, createOrderDTO, planDTO);
            MessageQueueDTO<SignatureDTO> messageQueueDTO = new MessageQueueDTO<SignatureDTO>(true, signatureDTO);

            publishMessageQueueService.run(paymentUrl, messageQueueDTO);
        } catch (RuntimeException e) {
            revokeIdempotencyId(user.getId());
            throw e;
        }
    }

    private void createIdempotencyId(String key) {
        log.info("Generate idempotency id...");
        boolean isSuccess = idempotencyService.create(key, idempotencyTime);

        if (!isSuccess) {
            log.error("Ocurred error when try generate idempotency id");
            throw new BadRequestException("Ocorreu um erro ao tentar efeturar a assinatura. Tente novamente mais tarde");
        }

        log.info("Idempotency id created");
    }

    private SignatureDTO createSignatureDTO(UserDTO user, CreateOrderDTO createOrderDTO, com.learnify.order.order.dto.plan.PlanDTO planDTO) {
        log.info("Creating signature dto...");
        CardDTO card = new CardDTO(
                createOrderDTO.cardNumber(),
                createOrderDTO.expMonth(),
                createOrderDTO.expYear(),
                createOrderDTO.cvc()
        );
        CustomerDTO customer = new CustomerDTO(user.getId(), user.getCustomerId());
        PlanDTO planData = new PlanDTO(
                planDTO.getId(),
                planDTO.getStripeId(),
                planDTO.getValue()
        );

        SignatureDTO signatureDTO = new SignatureDTO(customer, card, planData);

        log.info("Signature dto created");
        return signatureDTO;
    }

    // TODO: testar esse fluxo
    private void revokeIdempotencyId(String key) {
        log.info("Deleting idempotency id...");
        idempotencyService.remove(key);
        log.info("Id deleted");
    }
}

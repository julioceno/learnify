package com.learnify.order.order.service;

import com.learnify.order.common.exception.BadRequestException;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.CreateOrderDTO;
import com.learnify.order.order.dto.CreatePaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
public class CreateOrderService {
    @Value("${api.order.idempotency.time}")
    private int idempotencyTime;

    @Value("${aws.services.queue.url.payment}")
    private String paymentUrl;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(String userId, CreateOrderDTO createOrderDTO) {
        createIdempotencyId(userId);

        try {
            CreatePaymentDTO createPaymentDTO = new CreatePaymentDTO();
            BeanUtils.copyProperties(createOrderDTO, createPaymentDTO);
            createPaymentDTO.setUserId(userId);

            publishMessageQueueService.run(paymentUrl, createPaymentDTO);
        } catch (RuntimeException e) {
            revokeIdempotencyId(userId);
            throw e;
        }
    }

    private String generateKey(String userId, CreateOrderDTO createOrderDTO) {
        return format("%s-%s", userId, createOrderDTO.planId());
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

    // TODO: testar esse fluxo
    private void revokeIdempotencyId(String key) {
        log.info("Deleting idempotency id...");
        idempotencyService.remove(key);
        log.info("Id deleted");
    }
}

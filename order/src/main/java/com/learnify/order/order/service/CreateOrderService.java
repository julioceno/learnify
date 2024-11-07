package com.learnify.order.order.service;

import com.learnify.order.common.exception.BadRequestException;
import com.learnify.order.common.service.IdempotencyService;
import com.learnify.order.common.service.PublishMessageQueueService;
import com.learnify.order.order.dto.CreateOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
public class CreateOrderService {
    @Value("${api.order.idempotency.time}")
    private int idempotencyTime;

    @Value("${api.queue.payment_url}")
    private String paymentUrl;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;


    public void run(String userId, CreateOrderDTO createOrderDTO) {
        String key = generateKey(userId, createOrderDTO);
        createIdempotencyId(key);

        publishMessageQueueService.run(paymentUrl, createOrderDTO);
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
}

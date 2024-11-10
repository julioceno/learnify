package com.learnify.plans.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.plans.common.dto.MessageQueueDTO;
import com.learnify.plans.common.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Slf4j
public class PublishMessageQueueService {
    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private ObjectMapper objectMapper;

    public <T>void run(final String queueUrl, MessageQueueDTO<T> body) {
        log.info("Create object to send message");
        String converted = convertObjectToJson(body);
        var message = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(converted)
                .build();

        log.info("Object created, sending message to queue...");
        sqsAsyncClient.sendMessage(message);
        log.info("Messaged sent");
    }

    private <T>String convertObjectToJson(MessageQueueDTO<T> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            log.error("Ocurred error when try convert object to string", e);
            throw new BadRequestException("Ocorreu um erro ao prosseguir a operação, entre em contato com a equipe de suporte");
        }
    }
}

package com.learnify.order.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.order.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.AmazonSQS;

@Service
@Slf4j
public class PublishMessageQueueService {
    @Value("${queueUrl}")
    private String queueUrl;

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;

    public void run(final String queueUrl, Object body) {
        log.info("Create object to send message");
        String converted = convertObjectToJson(body);
        var message = new com.amazonaws.services.sqs.model.SendMessageRequest()
               .withQueueUrl(queueUrl)
               .withMessageBody(converted);

        log.info("Object created, sending message to queue...");
        amazonSQS.sendMessage(message);
        log.info("Messaged sent");
    }

    private String convertObjectToJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            log.error("Ocurred error when try convert object to string", e);
            throw new BadRequestException("Ocorreu um erro ao prosseguir a operação, entre em contato com a equipe de suporte");
        }
    }
}

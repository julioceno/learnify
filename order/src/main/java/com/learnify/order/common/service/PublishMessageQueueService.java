package com.learnify.order.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class PublicMessageQueueService {
    @Value("${queueUrl}")
    private String queueUrl;

    public void run() {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("hello world")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
    }
}

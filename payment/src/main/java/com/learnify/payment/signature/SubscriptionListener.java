package com.learnify.payment.signature;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.signature.dto.SignatureDTO;
import com.learnify.payment.signature.service.HandleCreateSubscriptionService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionListener {
    @Autowired
    private HandleCreateSubscriptionService handleCreateSubscriptionService;

    @SqsListener("${aws.services.queue.name.payment}")
    public void receiveMessages(MessageQueueDTO<SignatureDTO> signatureDTO) {
        handleCreateSubscriptionService.run(signatureDTO);
    }
}

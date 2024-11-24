package com.learnify.plans.signatures;

import com.learnify.plans.common.dto.MessageQueueDTO;
import com.learnify.plans.signatures.dto.SignatureDTO;
import com.learnify.plans.signatures.services.HandleCreateSignatureService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignatureListener {
    @Autowired
    private HandleCreateSignatureService createSignatureService;

    @SqsListener("${aws.services.queue.name.signature}")
    public void receiveMessages(MessageQueueDTO<SignatureDTO> signatureDTO) {
        createSignatureService.run(signatureDTO);
    }
}

package com.learnify.plans.signatures;

import com.learnify.plans.signatures.dto.SignatureDTO;
import com.learnify.plans.signatures.services.CreateSignatureService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignatureListener {
    @Autowired
    private CreateSignatureService createSignatureService;

    // TODO: adicionar no properties.yml
    @SqsListener("signature")
    public void receiveMessages(SignatureDTO signatureDTO) {
        createSignatureService.run(signatureDTO);
    }
}

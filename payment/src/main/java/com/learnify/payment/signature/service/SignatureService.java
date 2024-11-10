package com.learnify.payment.signature.service;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.SignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignatureService {
    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(SignatureDTO signatureDTO) {
        // TODO: criar lógica de assinatura no stripe

        MessageQueueDTO messageQueueDTO = new MessageQueueDTO(true, signatureDTO.userId());
        publishMessageQueueService.run("", messageQueueDTO);
    }
}

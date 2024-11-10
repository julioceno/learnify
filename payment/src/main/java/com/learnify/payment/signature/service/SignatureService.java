package com.learnify.payment.signature.service;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.ReturnPaymentDTO;
import com.learnify.payment.signature.dto.SignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignatureService {
    @Value("${aws.services.queue.url.return-payment}")
    private String returnPayment;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(SignatureDTO signatureDTO) {
        // TODO: criar lógica de assinatura no stripe

        log.info("Return payment...");
        ReturnPaymentDTO returnPaymentDTO = new ReturnPaymentDTO(signatureDTO.userId(), signatureDTO.planId());
        MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO = new MessageQueueDTO<ReturnPaymentDTO>(true, returnPaymentDTO);
        publishMessageQueueService.run(returnPayment, messageQueueDTO);
    }
}

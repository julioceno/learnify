package com.learnify.payment.signature;

import com.learnify.payment.signature.dto.PaymentDataDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SinatureListener {

    @SqsListener("signature_plan")
    public void receiveMessages(PaymentDataDTO object) {
        System.out.println(object.cardNumber());
        System.out.println(object.planId());
    }
}

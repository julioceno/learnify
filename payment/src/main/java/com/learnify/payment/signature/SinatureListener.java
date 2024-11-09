package com.learnify.payment.signature;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SinatureListener {
    @SqsListener("signature_plan")
    public void receiveMessages(Object object) {
        System.out.println(object);
    }
}

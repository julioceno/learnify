package com.learnify.plans.signatures;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SignatureListener {

    // TODO: adicionar no properties.yml
    @SqsListener("signature")
    public void receiveMessages(Object object) {
        System.out.println(object);
    }
}

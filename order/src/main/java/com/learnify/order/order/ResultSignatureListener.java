package com.learnify.order.order;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.order.service.HandleReturnSignatureService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultSignatureListener {
    @Autowired
    private HandleReturnSignatureService handleResultSignatureService;

    // TODO: pegar o nome do application.yml
    @SqsListener("return-signature")
    public void returnSignature(MessageQueueDTO messageQueueDTO) {
        handleResultSignatureService.run(messageQueueDTO);
    }
}

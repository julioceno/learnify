package com.learnify.order.order;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.order.service.HandleResultSignatureService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultSignatureListener {
    @Autowired
    private HandleResultSignatureService handleResultSignatureService;

    // TODO: pegar o nome do application.yml
    @SqsListener("result_signature")
    public void resultSignature(MessageQueueDTO messageQueueDTO) {
        handleResultSignatureService.run(messageQueueDTO);
    }
}

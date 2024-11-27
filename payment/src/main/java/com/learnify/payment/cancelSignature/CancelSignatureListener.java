package com.learnify.payment.cancelSignature;

import com.learnify.payment.cancelSignature.dto.CancelSignatureDTO;
import com.learnify.payment.cancelSignature.services.HandleCancelSignatureService;
import com.learnify.payment.common.dto.MessageQueueDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelSignatureListener {
    @Autowired
    private HandleCancelSignatureService cancelSignatureService;

    @SqsListener("${aws.services.queue.name.cancel-signature}")
    public void receiveMessages(MessageQueueDTO<CancelSignatureDTO> dto) {
        cancelSignatureService.run(dto);
    }
}

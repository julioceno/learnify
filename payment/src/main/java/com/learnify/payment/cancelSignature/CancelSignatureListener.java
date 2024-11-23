package com.learnify.payment.cancelSignature;

import com.learnify.payment.cancelSignature.dto.CancelSignatureDTO;
import com.learnify.payment.cancelSignature.services.CancelSignatureService;
import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.UserQueueDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelSignatureListener {
    @Autowired
    private CancelSignatureService cancelSignatureService;

    @SqsListener("${aws.services.queue.name.cancel-signature}")
    public void receiveMessages(MessageQueueDTO<CancelSignatureDTO> dto) {
        cancelSignatureService.run(dto);
    }
}

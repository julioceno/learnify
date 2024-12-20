package com.learnify.order.order;

import com.learnify.order.common.dto.MessageQueueDTO;
import com.learnify.order.order.dto.ReturnPaymentDTO;
import com.learnify.order.order.service.HandleReturnCancelSignatureService;
import com.learnify.order.order.service.HandleReturnPaymentService;
import com.learnify.order.order.service.HandleReturnSignatureService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderListener {
    private final HandleReturnSignatureService handleResultSignatureService;
    private final HandleReturnPaymentService handleReturnPaymentService;
    private final HandleReturnCancelSignatureService handleReturnCancelSignatureService;

    @SqsListener("${aws.services.queue.name.return-signature}")
    public void returnSignature(MessageQueueDTO<?> dto) {
        handleResultSignatureService.run(dto);
    }

    @SqsListener("${aws.services.queue.name.return-payment}")
    public void returnPayment(MessageQueueDTO<?> messageQueueDTO) {
        handleReturnPaymentService.run(messageQueueDTO);
    }

    @SqsListener("${aws.services.queue.name.return-cancel-signature}")
    public void cancelSignature(MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO) {
        handleReturnCancelSignatureService.run(messageQueueDTO);
    }
}

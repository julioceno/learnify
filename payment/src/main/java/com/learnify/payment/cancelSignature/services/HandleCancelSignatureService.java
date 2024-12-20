package com.learnify.payment.cancelSignature.services;

import com.learnify.payment.cancelSignature.dto.CancelSignatureDTO;
import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.ReturnErrorDTO;
import com.learnify.payment.common.exception.BadRequestException;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.ReturnPaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleCancelSignatureService {
    @Value("${aws.services.queue.url.return-cancel-signature}")
    private String returnCancelSignature;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(MessageQueueDTO<CancelSignatureDTO> dto)  {
        try {
            cancelSubscription(dto.data().subscriptionId());
            publishSuccessMessage(dto.data());
        } catch (BadRequestException e) {
            publishErrorMessage(e, dto.data());
        }
    }

    private void cancelSubscription(String subscriptionId) {
        try {
            log.info("Getting subscription with id {}...", subscriptionId);
            Subscription resource = Subscription.retrieve(subscriptionId);

            log.info("Subscription obtained, cancel subs*cription...");
            SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
            resource.cancel(params);

            log.info("Cancel concluded");
        } catch (StripeException e) {
            log.error("Occurred error when try cancel subscription");
            throw new BadRequestException("Não foi possível cancelar a assinatura");
        }
    }

    private void publishSuccessMessage(CancelSignatureDTO dto) {
        log.info("Publish success message...");
        ReturnPaymentDTO returnPaymentDTO = new ReturnPaymentDTO(dto.orderId(), dto.userId(), dto.subscriptionId());
        MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO = new MessageQueueDTO<ReturnPaymentDTO>(true, returnPaymentDTO);
        publishMessageQueueService.run(returnCancelSignature, messageQueueDTO);
    }

    private void publishErrorMessage(BadRequestException error, CancelSignatureDTO dto) {
        log.error("Publish error message...", error);
        ReturnErrorDTO returnErrorDTO = new ReturnErrorDTO(dto.orderId(), dto.userId(), error.getStatus(), error.getMessage());
        MessageQueueDTO<ReturnErrorDTO> messageQueueDTO = new MessageQueueDTO<ReturnErrorDTO>(false, returnErrorDTO);
        publishMessageQueueService.run(returnCancelSignature, messageQueueDTO);
    }
}

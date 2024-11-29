package com.learnify.payment.signature.service;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.ReturnErrorDTO;
import com.learnify.payment.common.exception.BadRequestException;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.ReturnPaymentDTO;
import com.learnify.payment.signature.dto.SignatureDTO;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class HandleCreateSubscriptionService {
    @Value("${aws.services.queue.url.return-payment}")
    private String returnPayment;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(MessageQueueDTO<SignatureDTO> signatureDTO) {
        try {
            Subscription subscription = createSubscription(signatureDTO.data());
            publishSuccessMessage(signatureDTO.data(), subscription.getId());
        } catch (BadRequestException e) {
            publishErrorMessage(e, signatureDTO.data());
        }
    }

    private Subscription createSubscription(SignatureDTO signatureDTO) {
        try {
            log.info("Creating subscription in user {}. Prepare recurring data...", signatureDTO.customer().userId());
            SubscriptionCreateParams.Item.PriceData.Recurring recurring = SubscriptionCreateParams.Item.PriceData.Recurring.builder()
                    .setInterval(SubscriptionCreateParams.Item.PriceData.Recurring.Interval.MONTH)
                    .build();

            log.info("Prepare price data params...");
            long valueInCents = signatureDTO.plan().value().multiply(BigDecimal.valueOf(100)).longValue();
            SubscriptionCreateParams.Item.PriceData priceData = SubscriptionCreateParams.Item.PriceData.builder()
                    .setCurrency("BRL")
                    .setUnitAmount(valueInCents)
                    .setProduct(signatureDTO.plan().stripeId())
                    .setRecurring(recurring)
                    .build();

            log.info("Prepare item params...");
            SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                    .setPriceData(priceData)
                    .build();

            log.info("Prepare subscription params...");
            SubscriptionCreateParams subscriptionParams = SubscriptionCreateParams.builder()
                    .setCustomer(signatureDTO.customer().customerId())
                    .addItem(item)
                    .build();

            log.info("Creating subscription...");
            Subscription subscription = Subscription.create(subscriptionParams);
            log.info("Subscription created");
            return subscription;
        } catch (com.stripe.exception.StripeException e) {
            throw new BadRequestException("Não foi possível criar a inscrição do usuário para esse plano");
        }
    }

    private void publishSuccessMessage(SignatureDTO signatureDTO, String subscriptionId) {
        log.info("Publish success message...");
        ReturnPaymentDTO returnPaymentDTO = new ReturnPaymentDTO(
                signatureDTO.orderId(),
                signatureDTO.customer().userId(),
                subscriptionId
        );
        MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO = new MessageQueueDTO<ReturnPaymentDTO>(true, returnPaymentDTO);
        publishMessageQueueService.run(returnPayment, messageQueueDTO);
    }

    private void publishErrorMessage(BadRequestException error, SignatureDTO dto) {
        log.error("Publish error message...", error);
        ReturnErrorDTO returnErrorDTO = new ReturnErrorDTO(
                dto.orderId(),
                dto.customer().userId(),
                error.getStatus(),
                error.getMessage()
        );

        MessageQueueDTO<ReturnErrorDTO> messageQueueDTO = new MessageQueueDTO<ReturnErrorDTO>(false, returnErrorDTO);
        publishMessageQueueService.run(returnPayment, messageQueueDTO);
    }
}

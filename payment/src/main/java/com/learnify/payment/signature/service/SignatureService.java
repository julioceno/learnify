package com.learnify.payment.signature.service;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.ReturnErrorDTO;
import com.learnify.payment.common.exception.BadRequestException;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.ReturnPaymentDTO;
import com.learnify.payment.signature.dto.SignatureDTO;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
// TODO: adicionar logs
public class SignatureService {
    @Value("${aws.services.queue.url.return-payment}")
    private String returnPayment;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(MessageQueueDTO<SignatureDTO> signatureDTO) {
        try {
            Customer resource = Customer.retrieve(signatureDTO.data().customer().customerId());

            resource.listPaymentMethods();
            createSubscription(signatureDTO.data());

            publishSuccessMessage(signatureDTO.data());
        } catch (BadRequestException e) {
            publishErrorMessage(e);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSubscription(SignatureDTO signatureDTO) {
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
            Subscription.create(subscriptionParams);
            log.info("Subscription created");
        } catch (com.stripe.exception.StripeException e) {
            throw new BadRequestException("Não foi possível criar a inscrição do usuário para esse plano");
        }
    }

    private void publishSuccessMessage(SignatureDTO signatureDTO) {
        log.info("Publish success message...");
        ReturnPaymentDTO returnPaymentDTO = new ReturnPaymentDTO(signatureDTO.customer().userId(), signatureDTO.plan().planId());
        MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO = new MessageQueueDTO<ReturnPaymentDTO>(true, returnPaymentDTO);
        publishMessageQueueService.run(returnPayment, messageQueueDTO);
    }

    private void publishErrorMessage(BadRequestException error) {
        log.error("Publish error message...", error);
        ReturnErrorDTO returnErrorDTO = new ReturnErrorDTO(error.getStatus(), error.getMessage());
        MessageQueueDTO<ReturnErrorDTO> messageQueueDTO = new MessageQueueDTO<ReturnErrorDTO>(false, returnErrorDTO);
        publishMessageQueueService.run(returnPayment, messageQueueDTO);
    }
}

package com.learnify.payment.signature.service;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.ReturnErrorDTO;
import com.learnify.payment.common.exception.BadRequestException;
import com.learnify.payment.common.service.PublishMessageQueueService;
import com.learnify.payment.signature.dto.ReturnPaymentDTO;
import com.learnify.payment.signature.dto.SignatureDTO;
import com.stripe.StripeClient;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
// TODO: adicionar logs
public class SignatureService {
    @Value("${aws.services.queue.url.return-payment}")
    private String returnPayment;

    private final PublishMessageQueueService publishMessageQueueService;
    private final StripeClient stripeClient;

    public SignatureService(PublishMessageQueueService publishMessageQueueService, StripeClient stripeClient) {
        this.publishMessageQueueService = publishMessageQueueService;
        this.stripeClient = stripeClient;
    }

    public void run(MessageQueueDTO<SignatureDTO> signatureDTO) {
        try {
            PaymentMethod paymentMethod = createPaymentMethod(signatureDTO.data());
            updateDefaultPaymentMethod(signatureDTO.data().customer().customerId(), paymentMethod);
            createSubscription(signatureDTO.data());

            ReturnPaymentDTO returnPaymentDTO = new ReturnPaymentDTO(signatureDTO.data().customer().userId(), signatureDTO.data().plan().planId());
            MessageQueueDTO<ReturnPaymentDTO> messageQueueDTO = new MessageQueueDTO<ReturnPaymentDTO>(true, returnPaymentDTO);
            publishMessageQueueService.run(returnPayment, messageQueueDTO);
        } catch (BadRequestException e) {
            ReturnErrorDTO returnErrorDTO = new ReturnErrorDTO(e.getStatus(), e.getMessage());
            MessageQueueDTO<ReturnErrorDTO> messageQueueDTO = new MessageQueueDTO<ReturnErrorDTO>(false, returnErrorDTO);
            publishMessageQueueService.run(returnPayment, messageQueueDTO);
        }
    }

    private PaymentMethod createPaymentMethod(SignatureDTO signatureDTO) {
        try {
            Long expMonthParsed = Long.parseLong(signatureDTO.card().expMonth());
            PaymentMethodCreateParams.CardDetails cardDetails =  PaymentMethodCreateParams.CardDetails.builder()
                    .setCvc(signatureDTO.card().cvc())
                    .setExpMonth(expMonthParsed)
                    .setNumber(signatureDTO.card().cardNumber())
                    .build();

            PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(cardDetails)
                    .setCustomer(signatureDTO.customer().customerId())
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(params);
            return paymentMethod;
        } catch (com.stripe.exception.StripeException e) {
            throw new BadRequestException("Não foi possível processar o meio de pagamento");
        }
    }

    private void updateDefaultPaymentMethod(String customerId, PaymentMethod paymentMethod) {
        try {
            Customer resource = Customer.retrieve(customerId);
            CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
                    .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                            .setDefaultPaymentMethod(paymentMethod.getId())
                            .build())
                    .build();

            resource.update(updateParams);
        } catch (com.stripe.exception.StripeException e) {
            throw new BadRequestException("Não foi possível setar o meio de pagamento por padrão do cliente");
        }
    }

    private void createSubscription(SignatureDTO signatureDTO) {
        try {
            SubscriptionCreateParams.Item.PriceData.Recurring recurring = SubscriptionCreateParams.Item.PriceData.Recurring.builder()
                    .setInterval(SubscriptionCreateParams.Item.PriceData.Recurring.Interval.MONTH)
                    .build();

            long valueInCents = signatureDTO.plan().value() * 100;
            SubscriptionCreateParams.Item.PriceData priceData = SubscriptionCreateParams.Item.PriceData.builder()
                    .setCurrency("BRL")
                    .setUnitAmount(valueInCents)
                    .setProduct(signatureDTO.plan().stripeId())
                    .setRecurring(recurring)
                    .build();

            SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                    .setPriceData(priceData)
                    .build();

            SubscriptionCreateParams subscriptionParams = SubscriptionCreateParams.builder()
                    .setCustomer(signatureDTO.customer().customerId())
                    .addItem(item)
                    .build();

            Subscription.create(subscriptionParams);
        } catch (com.stripe.exception.StripeException e) {
            throw new BadRequestException("Não foi possível criar a inscrição do usuário para esse plano");
        }
    }
}

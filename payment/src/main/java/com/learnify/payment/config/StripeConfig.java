package com.learnify.payment.config;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Bean
    public StripeClient stripeClient() {
        Stripe.apiKey = apiKey;
        return new StripeClient(apiKey);
    }
}

package com.learnify.sso.config;

import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Bean
    public StripeClient stripeClient() {
        return new StripeClient(apiKey);
    }
}

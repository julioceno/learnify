package com.learnify.order.infra.configuration;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SqsConfiguration {

    @Bean
    public AmazonSQS amazonAsyncHttpClient() {
        return AmazonSQSClientBuilder.defaultClient();
   }
}

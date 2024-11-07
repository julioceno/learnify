package com.learnify.order.infra.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfiguration {
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}

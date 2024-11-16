package com.learnify.payment.signature.dto;

public record PlanDTO(
        String planId,
        String planStripeId,
        Long value
) {
}

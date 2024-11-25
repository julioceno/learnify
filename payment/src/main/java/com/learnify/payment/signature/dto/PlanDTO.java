package com.learnify.payment.signature.dto;

import java.math.BigDecimal;

public record PlanDTO(
        String stripeId,
        BigDecimal value
) {
}

package com.learnify.order.order.dto.payment;

import java.math.BigDecimal;

public record PlanDTO(
        String planId,
        String stripeId,
        BigDecimal value
) {
}

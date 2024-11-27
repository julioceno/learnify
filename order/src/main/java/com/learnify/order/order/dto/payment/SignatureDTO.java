package com.learnify.order.order.dto.payment;

public record SignatureDTO(
    String orderId,
    CustomerDTO customer,
    PlanDTO plan
) {}


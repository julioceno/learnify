package com.learnify.order.order.dto.payment;

public record SignatureDTO(
    CustomerDTO customer,
    PlanDTO plan
) {}


package com.learnify.order.order.dto.payment;

public record SignatureDTO(
    CustomerDTO customer,
    CardDTO card,
    PlanDTO plan
) {}


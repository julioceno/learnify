package com.learnify.payment.signature.dto;

public record SignatureDTO(
    String orderId,
    CustomerDTO customer,
    PlanDTO plan
) {}


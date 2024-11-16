package com.learnify.payment.signature.dto;

public record SignatureDTO(
    CustomerDTO customer,
    CardDTO card,
    PlanDTO plan
) {}


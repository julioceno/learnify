package com.learnify.payment.signature.dto;

public record SignatureDTO(
        String userId,
        String planId,
        String cardNumber,
        String expMonth,
        String expYear,
        String cvc
) {}

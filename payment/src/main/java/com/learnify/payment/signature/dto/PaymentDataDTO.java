package com.learnify.payment.signature.dto;

public record PaymentDataDTO(
        String planId,
        String cardNumber,
        String expMonth,
        String expYear,
        String cvc
) {}

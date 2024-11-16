package com.learnify.payment.signature.dto;

public record CardDTO(
        String cardNumber,
        String expMonth,
        String expYear,
        String cvc
) {}

package com.learnify.order.order.dto.payment;

public record CardDTO(
        String cardNumber,
        String expMonth,
        String expYear,
        String cvc
) {}

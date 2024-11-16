package com.learnify.payment.signature.dto;

public record CustomerDTO (
    String userId,
    String customerId,
    String name,
    String email
) {}

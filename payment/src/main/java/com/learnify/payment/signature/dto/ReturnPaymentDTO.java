package com.learnify.payment.signature.dto;

public record ReturnPaymentDTO (
        String orderId,
        String userId,
        String subscriptionId
){
}

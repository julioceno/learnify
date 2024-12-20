package com.learnify.payment.common.dto;

import org.springframework.http.HttpStatus;

public record ReturnErrorDTO(String orderId, String userId, HttpStatus code, String message) {
}

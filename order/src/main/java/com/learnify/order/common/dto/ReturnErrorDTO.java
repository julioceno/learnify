package com.learnify.order.common.dto;

import org.springframework.http.HttpStatus;

public record ReturnErrorDTO(String orderId, String userId, HttpStatus code, String message) {
}
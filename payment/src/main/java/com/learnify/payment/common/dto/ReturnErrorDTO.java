package com.learnify.payment.common.dto;

import org.springframework.http.HttpStatus;

public record ReturnErrorDTO(HttpStatus code, String message) {
}

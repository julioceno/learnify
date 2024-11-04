package com.learnify.gateway.common.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends StatusErrorException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException() {
        super(status, "Ocorreu um erro, entre em contato com o suporte");
    }
    public BadRequestException(String message) {
        super(status, message);
    }
}

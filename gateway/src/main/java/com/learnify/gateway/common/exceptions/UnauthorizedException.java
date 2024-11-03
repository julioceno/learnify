package com.learnify.gateway.common.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends StatusErrorException {
    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException() {
        super(status, "Não autorizado.");
    }
    public UnauthorizedException(String message) {
        super(status, message);
    }
}

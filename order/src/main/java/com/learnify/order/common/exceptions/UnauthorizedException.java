package com.learnify.order.common.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Não autorizado.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

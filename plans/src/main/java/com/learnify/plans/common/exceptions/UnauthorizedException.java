package com.learnify.plans.common.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Não autorizado.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

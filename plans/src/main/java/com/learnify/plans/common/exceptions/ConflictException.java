package com.learnify.plans.common.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends StatusErrorException {
    private static final HttpStatus status = HttpStatus.CONFLICT;
    public ConflictException(String msg) {
        super(status, msg);
    }
}
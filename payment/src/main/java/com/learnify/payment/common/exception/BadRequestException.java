package com.learnify.payment.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends StatusErrorException  {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException(String msg) {
        super(status, msg);
    }
}
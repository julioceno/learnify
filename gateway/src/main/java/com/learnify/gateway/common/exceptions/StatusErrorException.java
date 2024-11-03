package com.learnify.gateway.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public abstract class StatusErrorException extends RuntimeException{
    private final HttpStatus status;
    private final String message;
}

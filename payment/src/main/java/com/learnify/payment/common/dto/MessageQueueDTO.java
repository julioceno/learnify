package com.learnify.payment.common.dto;

public record MessageQueueDTO<T>(
        Boolean ok,
        T data
){}

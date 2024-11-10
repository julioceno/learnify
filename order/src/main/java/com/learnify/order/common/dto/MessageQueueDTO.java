package com.learnify.order.common.dto;

public record MessageQueueDTO<T>(
        Boolean ok,
        T data
){}

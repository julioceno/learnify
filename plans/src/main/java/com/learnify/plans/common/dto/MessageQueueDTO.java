package com.learnify.plans.common.dto;

public record MessageQueueDTO<T>(
        Boolean ok,
        T data
){}

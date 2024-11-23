package com.learnify.order.order.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDTO (
        @NotBlank(message =  "ID do plano é obrigatório")
        String planId
) {}

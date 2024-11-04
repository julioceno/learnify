package com.learnify.order.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// TODO: validar se as mensagens de DTO estao no padrão
public record CreateOrderDTO (
        @NotBlank(message = "O ID do plano não pode estar vazio.")
        String planId,

        @NotBlank(message = "O número do cartão não pode estar vazio.")
        @Size(min = 16, max = 19, message = "O número do cartão deve ter entre 16 e 19 dígitos.")
        String cardNumber,

        @NotBlank(message = "O mês de validade não pode estar vazio.")
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "O mês de validade deve ser um número entre 01 e 12.")
        String expMonth,

        @NotBlank(message = "O ano de validade não pode estar vazio.")
        @Pattern(regexp = "^[0-9]{4}$", message = "O ano de validade deve ser um número de 4 dígitos.")
        String expYear,

        @NotBlank(message = "O código de segurança (CVC) não pode estar vazio.")
        @Size(min = 3, max = 4, message = "O código de segurança deve ter entre 3 e 4 dígitos.")
        String cvc
) {}

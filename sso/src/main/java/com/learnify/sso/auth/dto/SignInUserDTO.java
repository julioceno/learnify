package com.learnify.sso.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
        
public record SignInUserDTO (
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password
) {}

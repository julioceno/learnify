package com.learnify.sso.users.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateUserDTO {
        @Valid

        @NotBlank(message = "Nome é obrigatório")
        private String name;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "É necessário que a senha tenha 8 caracteres")
        private String password;
}
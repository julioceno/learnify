package com.learnify.sso.users.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateUserDTO {
        @NotBlank(message = "Nome é obrigatório")
        private String name;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        private String email;

        @Size(min = 8, message = "É necessário que a senha tenha 8 caracteres")
        private String password;
}
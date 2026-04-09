package com.alexsantosportfolio.emailcontactservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnviarEmailRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nomeEmail,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "Assunto é obrigatório")
        @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
        String assuntoEmail,

        @NotBlank(message = "Mensagem é obrigatória")
        @Size(max = 2000, message = "Mensagem deve ter no máximo 2000 caracteres")
        String mensagemEmail
) {}

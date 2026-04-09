package com.alexsantosportfolio.emailcontactservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para enviar um email de contato")
public record EnviarEmailRequest(

        @Schema(description = "Nome completo do remetente",
                example = "João Silva",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nomeEmail,

        @Schema(description = "Endereço de email do remetente",
                example = "joao.silva@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 255)
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
        String email,

        @Schema(description = "Assunto do email",
                example = "Orçamento de projeto",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 200)
        @NotBlank(message = "Assunto é obrigatório")
        @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
        String assuntoEmail,

        @Schema(description = "Corpo da mensagem do email",
                example = "Olá, gostaria de saber mais sobre seus serviços de desenvolvimento web.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 2000)
        @NotBlank(message = "Mensagem é obrigatória")
        @Size(max = 2000, message = "Mensagem deve ter no máximo 2000 caracteres")
        String mensagemEmail
) {}

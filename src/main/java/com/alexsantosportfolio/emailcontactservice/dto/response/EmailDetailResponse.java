package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes de um registro de email")
public record EmailDetailResponse(

        @Schema(description = "Identificador único do registro",
                example = "f1b8f24a-7a68-4c16-9fd7-11d1b4d2ef9c")
        UUID id,

        @Schema(description = "Nome do remetente",
                example = "João Silva")
        String nomeEmail,

        @Schema(description = "Endereço de email do remetente",
                example = "joao.silva@email.com")
        String email,

        @Schema(description = "Assunto do email",
                example = "Orçamento de projeto")
        String assuntoEmail,

        @Schema(description = "Conteúdo da mensagem",
                example = "Olá, gostaria de saber mais sobre seus serviços.")
        String mensagemEmail,

        @Schema(description = "Data e hora do envio",
                example = "2026-04-09T01:30:00",
                type = "string", format = "date-time")
        LocalDateTime dataEnvio,

        @Schema(description = "Indica se o email foi enviado com sucesso",
                example = "true")
        boolean enviadoComSucesso
) {
    public static EmailDetailResponse from(EmailEntity entity) {
        return new EmailDetailResponse(
                entity.getId(),
                entity.getNomeEmail(),
                entity.getEmail(),
                entity.getAssuntoEmail(),
                entity.getMensagemEmail(),
                entity.getDataEnvio(),
                entity.getEnviadoComSucesso()
        );
    }
}

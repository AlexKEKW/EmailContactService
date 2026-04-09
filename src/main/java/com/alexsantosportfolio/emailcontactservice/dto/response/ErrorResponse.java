package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de erro da API")
public record ErrorResponse(

        @Schema(description = "Sempre false para respostas de erro",
                example = "false")
        boolean success,

        @Schema(description = "Mensagem descritiva do erro",
                example = "Erro de validação: email: Email inválido")
        String message,

        @Schema(description = "Data e hora em que o erro ocorreu",
                example = "2026-04-09T01:30:00",
                type = "string", format = "date-time")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(false, message, LocalDateTime.now());
    }
}

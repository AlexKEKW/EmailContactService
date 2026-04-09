package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de sucesso após operação com email")
public record EmailResponse(

        @Schema(description = "Indica se a operação foi bem-sucedida",
                example = "true")
        boolean success,

        @Schema(description = "Mensagem descritiva do resultado",
                example = "Email enviado com sucesso")
        String message,

        @Schema(description = "Data e hora da operação",
                example = "2026-04-09T01:30:00",
                type = "string", format = "date-time")
        LocalDateTime timestamp
) {
    public static EmailResponse success(String message) {
        return new EmailResponse(true, message, LocalDateTime.now());
    }
}

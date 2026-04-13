package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta contendo os detalhes de um registro de email.
 *
 * <p>Utilizado nos endpoints de listagem e busca por ID. Expõe os
 * campos fixos do email e os campos dinâmicos adicionais submetidos
 * pelo formulário frontend.</p>
 *
 * @see EmailEntity
 */
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
        boolean enviadoComSucesso,

        @Schema(description = "Campos extras dinâmicos submetidos pelo formulário",
                example = "{\"telefone\": \"11999999999\", \"empresa\": \"TechCorp\"}")
        Map<String, Object> camposAdicionais
) {
    /**
     * Factory method que converte uma {@link EmailEntity} para este DTO de resposta.
     *
     * @param entity entidade JPA com os dados do email persistido
     * @return instância de {@code EmailDetailResponse} pronta para serialização
     */
    public static EmailDetailResponse from(EmailEntity entity) {
        return new EmailDetailResponse(
                entity.getId(),
                entity.getNomeEmail(),
                entity.getEmail(),
                entity.getAssuntoEmail(),
                entity.getMensagemEmail(),
                entity.getDataEnvio(),
                entity.getEnviadoComSucesso(),
                entity.getCamposAdicionais()
        );
    }
}

package com.alexsantosportfolio.emailcontactservice.dto.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para o envio de emails de contato via formulário.
 *
 * <p>Além dos campos fixos obrigatórios ({@code nomeEmail}, {@code email},
 * {@code assuntoEmail}, {@code mensagemEmail}), este record captura
 * automaticamente qualquer campo extra enviado pelo frontend através
 * do mecanismo {@link JsonAnySetter} do Jackson.</p>
 *
 * <p>Isso permite que formulários de contato enviem campos dinâmicos
 * (ex: telefone, empresa, como_nos_conheceu) sem necessidade de
 * alterar o backend.</p>
 *
 * <p><b>Exemplo de payload:</b></p>
 * <pre>{@code
 * {
 *   "nomeEmail": "João Silva",
 *   "email": "joao@email.com",
 *   "assuntoEmail": "Orçamento",
 *   "mensagemEmail": "Olá!",
 *   "telefone": "11999999999",
 *   "empresa": "TechCorp"
 * }
 * }</pre>
 *
 * <p>Os campos {@code telefone} e {@code empresa} serão capturados
 * automaticamente no Map {@code camposAdicionais}.</p>
 */
@Schema(description = "Dados necessários para enviar um email de contato. "
        + "Aceita campos extras dinâmicos além dos obrigatórios.")
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
        String mensagemEmail,

        @Schema(description = "Campos extras dinâmicos enviados pelo formulário",
                example = "{\"telefone\": \"11999999999\", \"empresa\": \"TechCorp\"}",
                hidden = true)
        @JsonAnySetter
        Map<String, Object> camposAdicionais
) {
    /**
     * Construtor compacto que garante a inicialização segura do Map de campos
     * adicionais como um HashMap vazio caso nenhum campo extra seja enviado.
     * Também aplica validação de segurança limitando a quantidade máxima de
     * campos dinâmicos e o tamanho dos valores.
     *
     * @throws IllegalArgumentException se o número de campos extras ultrapassar 20
     *                                  ou se algum valor serializado exceder 1000 caracteres
     */
    public EnviarEmailRequest {
        if (camposAdicionais == null) {
            camposAdicionais = new HashMap<>();
        }

        if (camposAdicionais.size() > 20) {
            throw new IllegalArgumentException(
                    "Número máximo de campos extras é 20. Recebidos: " + camposAdicionais.size());
        }

        for (Map.Entry<String, Object> entry : camposAdicionais.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().length() > 1000) {
                throw new IllegalArgumentException(
                        "Campo '" + entry.getKey() + "' excede o limite de 1000 caracteres");
            }
        }
    }

    /**
     * Retorna uma cópia imutável dos campos adicionais para evitar
     * modificações externas no estado interno do record.
     *
     * @return mapa imutável dos campos dinâmicos extras
     */
    @Override
    public Map<String, Object> camposAdicionais() {
        return Collections.unmodifiableMap(camposAdicionais);
    }
}

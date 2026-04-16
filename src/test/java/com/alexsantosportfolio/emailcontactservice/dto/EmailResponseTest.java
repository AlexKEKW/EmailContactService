package com.alexsantosportfolio.emailcontactservice.dto;

import com.alexsantosportfolio.emailcontactservice.dto.response.EmailResponse;
import com.alexsantosportfolio.emailcontactservice.dto.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de resposta — EmailResponse e ErrorResponse")
class EmailResponseTest {

    @Nested
    @DisplayName("EmailResponse.success()")
    class EmailResponseSuccess {

        @Test
        @DisplayName("Deve criar resposta de sucesso com success=true e mensagem")
        void deveCriarRespostaDeSucesso() {
            EmailResponse response = EmailResponse.success("Email enviado com sucesso");

            assertThat(response.success()).isTrue();
            assertThat(response.message()).isEqualTo("Email enviado com sucesso");
            assertThat(response.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Deve gerar timestamp no momento da criação")
        void deveGerarTimestampAtual() {
            EmailResponse response = EmailResponse.success("Teste");

            assertThat(response.timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("ErrorResponse.of()")
    class ErrorResponseOf {

        @Test
        @DisplayName("Deve criar resposta de erro com success=false e mensagem")
        void deveCriarRespostaDeErro() {
            ErrorResponse response = ErrorResponse.of("Erro de validação: email inválido");

            assertThat(response.success()).isFalse();
            assertThat(response.message()).isEqualTo("Erro de validação: email inválido");
            assertThat(response.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Deve gerar timestamp no momento da criação")
        void deveGerarTimestampAtual() {
            ErrorResponse response = ErrorResponse.of("Erro");

            assertThat(response.timestamp()).isNotNull();
        }
    }
}

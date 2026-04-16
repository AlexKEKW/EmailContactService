package com.alexsantosportfolio.emailcontactservice.dto;

import com.alexsantosportfolio.emailcontactservice.dto.request.EnviarEmailRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EnviarEmailRequest — validação e construtor compacto")
class EnviarEmailRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private EnviarEmailRequest criarRequestValido() {
        return new EnviarEmailRequest(
                "João Silva",
                "joao@email.com",
                "Assunto de teste",
                "Mensagem de teste",
                null
        );
    }

    @Nested
    @DisplayName("Bean Validation")
    class BeanValidation {

        @Test
        @DisplayName("Deve aceitar request válido com todos os campos obrigatórios")
        void deveAceitarRequestValido() {
            var request = criarRequestValido();

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Deve rejeitar nomeEmail em branco")
        void deveRejeitarNomeEmailEmBranco() {
            var request = new EnviarEmailRequest("", "joao@email.com", "Assunto", "Msg", null);

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nomeEmail"));
        }

        @Test
        @DisplayName("Deve rejeitar email com formato inválido")
        void deveRejeitarEmailInvalido() {
            var request = new EnviarEmailRequest("João", "email-invalido", "Assunto", "Msg", null);

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }

        @Test
        @DisplayName("Deve rejeitar assuntoEmail com mais de 200 caracteres")
        void deveRejeitarAssuntoMuitoLongo() {
            String assuntoLongo = "A".repeat(201);
            var request = new EnviarEmailRequest("João", "joao@email.com", assuntoLongo, "Msg", null);

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("assuntoEmail"));
        }

        @Test
        @DisplayName("Deve rejeitar mensagemEmail em branco")
        void deveRejeitarMensagemEmBranco() {
            var request = new EnviarEmailRequest("João", "joao@email.com", "Assunto", "", null);

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("mensagemEmail"));
        }

        @Test
        @DisplayName("Deve rejeitar mensagemEmail com mais de 2000 caracteres")
        void deveRejeitarMensagemMuitoLonga() {
            String mensagemLonga = "A".repeat(2001);
            var request = new EnviarEmailRequest("João", "joao@email.com", "Assunto", mensagemLonga, null);

            Set<ConstraintViolation<EnviarEmailRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("mensagemEmail"));
        }
    }

    @Nested
    @DisplayName("Construtor Compacto — campos adicionais")
    class ConstrutorCompacto {

        @Test
        @DisplayName("Deve inicializar camposAdicionais como HashMap vazio quando null")
        void deveInicializarCamposAdicionaisComoVazioQuandoNull() {
            var request = criarRequestValido();

            assertThat(request.camposAdicionais()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException com mais de 20 campos adicionais")
        void deveLancarExcecaoComMaisDe20CamposAdicionais() {
            Map<String, Object> campos = new HashMap<>();
            for (int i = 0; i < 21; i++) {
                campos.put("campo" + i, "valor" + i);
            }

            assertThatThrownBy(() -> new EnviarEmailRequest("João", "joao@email.com", "Assunto", "Msg", campos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Número máximo de campos extras é 20");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando campo adicional excede 1000 caracteres")
        void deveLancarExcecaoQuandoCampoExcede1000Chars() {
            Map<String, Object> campos = new HashMap<>();
            campos.put("campoLongo", "A".repeat(1001));

            assertThatThrownBy(() -> new EnviarEmailRequest("João", "joao@email.com", "Assunto", "Msg", campos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("excede o limite de 1000 caracteres");
        }

        @Test
        @DisplayName("Deve aceitar exatamente 20 campos adicionais")
        void deveAceitarExatamente20CamposAdicionais() {
            Map<String, Object> campos = new HashMap<>();
            for (int i = 0; i < 20; i++) {
                campos.put("campo" + i, "valor" + i);
            }

            var request = new EnviarEmailRequest("João", "joao@email.com", "Assunto", "Msg", campos);

            assertThat(request.camposAdicionais()).hasSize(20);
        }

        @Test
        @DisplayName("Deve retornar mapa imutável do accessor camposAdicionais()")
        void deveRetornarMapaImutavel() {
            Map<String, Object> campos = new HashMap<>();
            campos.put("telefone", "11999999999");

            var request = new EnviarEmailRequest("João", "joao@email.com", "Assunto", "Msg", campos);

            assertThatThrownBy(() -> request.camposAdicionais().put("novo", "valor"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Deve aceitar campo adicional com valor null sem lançar exceção")
        void deveAceitarCampoAdicionalComValorNull() {
            Map<String, Object> campos = new HashMap<>();
            campos.put("campoNulo", null);

            var request = new EnviarEmailRequest("João", "joao@email.com", "Assunto", "Msg", campos);

            assertThat(request.camposAdicionais()).containsEntry("campoNulo", null);
        }
    }
}

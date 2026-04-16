package com.alexsantosportfolio.emailcontactservice.dto;

import com.alexsantosportfolio.emailcontactservice.dto.response.EmailDetailResponse;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailDetailResponse — factory method from()")
class EmailDetailResponseTest {

    @Test
    @DisplayName("Deve mapear todos os campos da entity corretamente")
    void deveMapearTodosOsCamposDaEntity() {
        UUID id = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();
        Map<String, Object> campos = Map.of("telefone", "11999999999");

        EmailEntity entity = new EmailEntity(
                "Maria Souza", "maria@email.com", "Projeto",
                "Detalhes do projeto", true, null, "192.168.1.1", campos
        );
        entity.setId(id);
        entity.setDataEnvio(agora);

        EmailDetailResponse response = EmailDetailResponse.from(entity);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nomeEmail()).isEqualTo("Maria Souza");
        assertThat(response.email()).isEqualTo("maria@email.com");
        assertThat(response.assuntoEmail()).isEqualTo("Projeto");
        assertThat(response.mensagemEmail()).isEqualTo("Detalhes do projeto");
        assertThat(response.dataEnvio()).isEqualTo(agora);
        assertThat(response.enviadoComSucesso()).isTrue();
        assertThat(response.camposAdicionais()).containsEntry("telefone", "11999999999");
    }

    @Test
    @DisplayName("Deve mapear entity com camposAdicionais null sem lançar exceção")
    void deveMapearEntityComCamposAdicionaisNull() {
        EmailEntity entity = new EmailEntity(
                "João", "joao@email.com", "Assunto",
                "Msg", false, "Timeout", "10.0.0.1", null
        );
        entity.setId(UUID.randomUUID());

        EmailDetailResponse response = EmailDetailResponse.from(entity);

        assertThat(response.camposAdicionais()).isNull();
        assertThat(response.enviadoComSucesso()).isFalse();
    }

    @Test
    @DisplayName("Deve mapear entity com dataEnvio null (antes de persistência)")
    void deveMapearEntityComDataEnvioNull() {
        EmailEntity entity = new EmailEntity(
                "Ana", "ana@email.com", "Teste",
                "Mensagem", true, null, "127.0.0.1", Map.of()
        );
        entity.setId(UUID.randomUUID());

        EmailDetailResponse response = EmailDetailResponse.from(entity);

        assertThat(response.dataEnvio()).isNull();
    }
}

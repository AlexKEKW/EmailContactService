package com.alexsantosportfolio.emailcontactservice.integration;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("EmailRepository — integração com PostgreSQL + JSONB")
class EmailRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EmailRepository emailRepository;

    @AfterEach
    void cleanUp() {
        emailRepository.deleteAll();
    }

    private EmailEntity criarEmailEntity(String nome, String email, String assunto,
                                          String mensagem, Map<String, Object> camposAdicionais) {
        return new EmailEntity(nome, email, assunto, mensagem,
                true, null, "127.0.0.1", camposAdicionais);
    }

    @Test
    @DisplayName("Deve persistir e recuperar entity com campos adicionais JSONB")
    void devePersistirERecuperarComJsonb() {
        Map<String, Object> campos = Map.of(
                "telefone", "11999999999",
                "empresa", "TechCorp"
        );
        EmailEntity entity = criarEmailEntity(
                "João", "joao@email.com", "Projeto", "Detalhes", campos
        );

        EmailEntity saved = emailRepository.save(entity);

        assertThat(saved.getId()).isNotNull();

        EmailEntity found = emailRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getCamposAdicionais()).isNotNull();
        assertThat(found.getCamposAdicionais()).containsEntry("telefone", "11999999999");
        assertThat(found.getCamposAdicionais()).containsEntry("empresa", "TechCorp");
    }

    @Test
    @DisplayName("Deve persistir entity com camposAdicionais null")
    void devePersistirComCamposAdicionaisNull() {
        EmailEntity entity = criarEmailEntity(
                "Ana", "ana@email.com", "Assunto", "Msg", null
        );

        EmailEntity saved = emailRepository.save(entity);
        EmailEntity found = emailRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getCamposAdicionais()).isNull();
    }

    @Test
    @DisplayName("Deve retornar todos os emails com findAll()")
    void deveRetornarTodosComFindAll() {
        emailRepository.save(criarEmailEntity("A", "a@e.com", "S1", "M1", null));
        emailRepository.save(criarEmailEntity("B", "b@e.com", "S2", "M2", null));
        emailRepository.save(criarEmailEntity("C", "c@e.com", "S3", "M3", null));

        List<EmailEntity> all = emailRepository.findAll();

        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Deve buscar emails por conteúdo no assunto")
    void deveBuscarPorConteudoNoAssunto() {
        emailRepository.save(criarEmailEntity("A", "a@e.com", "Orçamento de Software", "Msg", null));
        emailRepository.save(criarEmailEntity("B", "b@e.com", "Parceria comercial", "Msg", null));
        emailRepository.save(criarEmailEntity("C", "c@e.com", "Orçamento de Design", "Msg", null));

        List<EmailEntity> resultado = emailRepository.listaEmailsPorConteudo("Orçamento");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(e -> e.getAssuntoEmail().contains("Orçamento"));
    }

    @Test
    @DisplayName("Deve buscar emails por conteúdo na mensagem")
    void deveBuscarPorConteudoNaMensagem() {
        emailRepository.save(criarEmailEntity("A", "a@e.com", "Assunto1", "Preciso de um orçamento urgente", null));
        emailRepository.save(criarEmailEntity("B", "b@e.com", "Assunto2", "Mensagem normal", null));

        List<EmailEntity> resultado = emailRepository.listaEmailsPorConteudo("orçamento urgente");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getMensagemEmail()).contains("orçamento urgente");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum email contém o termo")
    void deveRetornarVazioQuandoSemMatch() {
        emailRepository.save(criarEmailEntity("A", "a@e.com", "Assunto", "Mensagem", null));

        List<EmailEntity> resultado = emailRepository.listaEmailsPorConteudo("inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar email por ID")
    void deveDeletarEmailPorId() {
        EmailEntity saved = emailRepository.save(
                criarEmailEntity("João", "joao@e.com", "A", "M", null)
        );

        emailRepository.deleteById(saved.getId());

        assertThat(emailRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Flyway migrations devem executar sem erro (schema criado)")
    void flywayMigrationsDevemExecutar() {
        // Se chegamos aqui, as migrations V1, V2, V3 rodaram com sucesso
        // Verificamos que a tabela existe tentando uma operação
        long count = emailRepository.count();
        // V2 insere dados de seed, então a contagem pode ser > 0
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}

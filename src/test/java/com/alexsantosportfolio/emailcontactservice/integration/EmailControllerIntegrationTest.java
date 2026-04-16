package com.alexsantosportfolio.emailcontactservice.integration;

import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.mail.internet.MimeMessage;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integração E2E — Controller → Service → Repository → PostgreSQL")
class EmailControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailRepository emailRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        // Configura o mock do JavaMailSender para retornar um MimeMessage real
        // (necessário porque MimeMessageHelper interage com o MimeMessage)
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @AfterEach
    void cleanUp() {
        emailRepository.deleteAll();
    }

    @Test
    @DisplayName("POST → persistência → GET por ID — fluxo completo de criação e consulta")
    void deveEnviarEmailEConsultarPorId() throws Exception {
        // POST — enviar email
        MvcResult postResult = mockMvc.perform(post("/v1/contato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeEmail": "João Silva",
                                    "email": "joao@email.com",
                                    "assuntoEmail": "Projeto Web",
                                    "mensagemEmail": "Gostaria de um orçamento"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        // Extrair ID do header Location
        String location = postResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);

        // GET por ID — verificar dados persistidos
        mockMvc.perform(get("/v1/contato/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeEmail").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.assuntoEmail").value("Projeto Web"))
                .andExpect(jsonPath("$.mensagemEmail").value("Gostaria de um orçamento"))
                .andExpect(jsonPath("$.enviadoComSucesso").value(true));
    }

    @Test
    @DisplayName("POST com campos dinâmicos → GET — JSONB persiste e retorna corretamente")
    void devePersistirERetornarCamposDinamicosJsonb() throws Exception {
        // POST com campos extras
        MvcResult postResult = mockMvc.perform(post("/v1/contato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeEmail": "Maria Souza",
                                    "email": "maria@email.com",
                                    "assuntoEmail": "Consultoria",
                                    "mensagemEmail": "Detalhes do projeto",
                                    "telefone": "11999999999",
                                    "empresa": "TechCorp",
                                    "comoNosConheceu": "Google"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = postResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);

        // Verificar que os campos dinâmicos foram persistidos via JSONB
        mockMvc.perform(get("/v1/contato/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeEmail").value("Maria Souza"))
                .andExpect(jsonPath("$.camposAdicionais.telefone").value("11999999999"))
                .andExpect(jsonPath("$.camposAdicionais.empresa").value("TechCorp"))
                .andExpect(jsonPath("$.camposAdicionais.comoNosConheceu").value("Google"));
    }

    @Test
    @DisplayName("POST → GET listagem — email aparece na lista")
    void deveAparecerNaListagemAposEnvio() throws Exception {
        // Enviar email
        mockMvc.perform(post("/v1/contato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeEmail": "Pedro Costa",
                                    "email": "pedro@email.com",
                                    "assuntoEmail": "Parceria",
                                    "mensagemEmail": "Proposta de parceria"
                                }
                                """))
                .andExpect(status().isCreated());

        // Verificar na listagem
        mockMvc.perform(get("/v1/contato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.nomeEmail == 'Pedro Costa')]").exists());
    }

    @Test
    @DisplayName("POST → DELETE → GET 404 — fluxo completo de exclusão")
    void deveRetornar404AposDelecao() throws Exception {
        // Enviar email
        MvcResult postResult = mockMvc.perform(post("/v1/contato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeEmail": "Ana Oliveira",
                                    "email": "ana@email.com",
                                    "assuntoEmail": "Teste delete",
                                    "mensagemEmail": "Email para ser deletado"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = postResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);

        // Confirmar que existe
        mockMvc.perform(get("/v1/contato/{id}", id))
                .andExpect(status().isOk());

        // Deletar
        mockMvc.perform(delete("/v1/contato/{id}", id))
                .andExpect(status().isNoContent());

        // Confirmar que não existe mais
        mockMvc.perform(get("/v1/contato/{id}", id))
                .andExpect(status().isNotFound());
    }
}

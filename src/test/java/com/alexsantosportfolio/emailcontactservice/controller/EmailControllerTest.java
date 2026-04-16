package com.alexsantosportfolio.emailcontactservice.controller;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.exception.EmailSendException;
import com.alexsantosportfolio.emailcontactservice.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
@DisplayName("EmailController — endpoints REST")
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    private EmailEntity criarEmailEntity(UUID id) {
        EmailEntity entity = new EmailEntity(
                "João Silva", "joao@email.com", "Assunto Teste",
                "Mensagem de teste", true, null, "192.168.1.1",
                Map.of("telefone", "11999999999")
        );
        entity.setId(id);
        entity.setDataEnvio(LocalDateTime.of(2026, 4, 9, 1, 30));
        return entity;
    }

    @Nested
    @DisplayName("POST /v1/contato")
    class PostContato {

        @Test
        @DisplayName("Deve enviar email e retornar 201 com header Location")
        void deveEnviarEmailERetornar201() throws Exception {
            UUID id = UUID.randomUUID();
            EmailEntity emailSalvo = criarEmailEntity(id);
            when(emailService.enviarEmail(any())).thenReturn(emailSalvo);

            mockMvc.perform(post("/v1/contato")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "nomeEmail": "João Silva",
                                        "email": "joao@email.com",
                                        "assuntoEmail": "Assunto Teste",
                                        "mensagemEmail": "Mensagem de teste"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", containsString(id.toString())))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Email enviado com sucesso"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("Deve retornar 400 quando body tem campos obrigatórios ausentes")
        void deveRetornar400QuandoCamposObrigatoriosAusentes() throws Exception {
            mockMvc.perform(post("/v1/contato")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "nomeEmail": "",
                                        "email": "",
                                        "assuntoEmail": "",
                                        "mensagemEmail": ""
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").isNotEmpty());

            verify(emailService, never()).enviarEmail(any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando email tem formato inválido")
        void deveRetornar400QuandoEmailInvalido() throws Exception {
            mockMvc.perform(post("/v1/contato")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "nomeEmail": "João",
                                        "email": "nao-eh-email",
                                        "assuntoEmail": "Teste",
                                        "mensagemEmail": "Msg"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message", containsString("email")));
        }

        @Test
        @DisplayName("Deve retornar 502 quando SMTP falhar")
        void deveRetornar502QuandoSmtpFalhar() throws Exception {
            when(emailService.enviarEmail(any()))
                    .thenThrow(new EmailSendException("Erro ao enviar email", new RuntimeException("SMTP timeout")));

            mockMvc.perform(post("/v1/contato")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "nomeEmail": "João",
                                        "email": "joao@email.com",
                                        "assuntoEmail": "Teste",
                                        "mensagemEmail": "Mensagem"
                                    }
                                    """))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro ao enviar email"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando IllegalArgumentException (campos adicionais excedidos)")
        void deveRetornar400QuandoIllegalArgument() throws Exception {
            when(emailService.enviarEmail(any()))
                    .thenThrow(new IllegalArgumentException("Número máximo de campos extras é 20"));

            mockMvc.perform(post("/v1/contato")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "nomeEmail": "João",
                                        "email": "joao@email.com",
                                        "assuntoEmail": "Teste",
                                        "mensagemEmail": "Mensagem"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message", containsString("Número máximo")));
        }
    }

    @Nested
    @DisplayName("GET /v1/contato")
    class GetContatos {

        @Test
        @DisplayName("Deve listar emails e retornar 200")
        void deveListarEmailsERetornar200() throws Exception {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            when(emailService.listaEmails()).thenReturn(List.of(
                    criarEmailEntity(id1), criarEmailEntity(id2)
            ));

            mockMvc.perform(get("/v1/contato"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").isNotEmpty())
                    .andExpect(jsonPath("$[0].nomeEmail").value("João Silva"))
                    .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                    .andExpect(jsonPath("$[0].enviadoComSucesso").value(true));
        }

        @Test
        @DisplayName("Deve retornar array vazio quando não há emails")
        void deveRetornarArrayVazio() throws Exception {
            when(emailService.listaEmails()).thenReturn(List.of());

            mockMvc.perform(get("/v1/contato"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /v1/contato/{id}")
    class GetContatoPorId {

        @Test
        @DisplayName("Deve retornar email específico com 200")
        void deveRetornarEmailEspecifico() throws Exception {
            UUID id = UUID.randomUUID();
            EmailEntity email = criarEmailEntity(id);
            when(emailService.buscarEmailPorId(id)).thenReturn(email);

            mockMvc.perform(get("/v1/contato/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nomeEmail").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"))
                    .andExpect(jsonPath("$.assuntoEmail").value("Assunto Teste"))
                    .andExpect(jsonPath("$.mensagemEmail").value("Mensagem de teste"))
                    .andExpect(jsonPath("$.enviadoComSucesso").value(true))
                    .andExpect(jsonPath("$.camposAdicionais.telefone").value("11999999999"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando email não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            UUID id = UUID.randomUUID();
            when(emailService.buscarEmailPorId(id))
                    .thenThrow(new EntityNotFoundException("Email não encontrado com id: " + id));

            mockMvc.perform(get("/v1/contato/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message", containsString("Email não encontrado")));
        }
    }

    @Nested
    @DisplayName("DELETE /v1/contato/{id}")
    class DeleteContato {

        @Test
        @DisplayName("Deve deletar email e retornar 204")
        void deveDeletarEmailERetornar204() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(emailService).deletarEmailPorId(id);

            mockMvc.perform(delete("/v1/contato/{id}", id))
                    .andExpect(status().isNoContent());

            verify(emailService).deletarEmailPorId(id);
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar email inexistente")
        void deveRetornar404AoDeletarInexistente() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new EntityNotFoundException("Email não encontrado para ser deletado"))
                    .when(emailService).deletarEmailPorId(id);

            mockMvc.perform(delete("/v1/contato/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message", containsString("Email não encontrado")));
        }
    }
}

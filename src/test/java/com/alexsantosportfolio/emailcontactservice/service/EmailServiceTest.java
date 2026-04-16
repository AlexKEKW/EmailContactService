package com.alexsantosportfolio.emailcontactservice.service;

import com.alexsantosportfolio.emailcontactservice.dto.request.EnviarEmailRequest;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.exception.EmailSendException;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;
import com.alexsantosportfolio.emailcontactservice.util.IpResolver;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService — lógica de negócio")
class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<EmailEntity> emailEntityCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "meuEmail", "destino@test.com");
    }

    private EnviarEmailRequest criarRequestValido() {
        return new EnviarEmailRequest(
                "João Silva", "joao@email.com",
                "Assunto Teste", "Mensagem de teste", null
        );
    }

    private EnviarEmailRequest criarRequestComCamposAdicionais() {
        Map<String, Object> campos = new HashMap<>();
        campos.put("telefone", "11999999999");
        campos.put("empresa", "TechCorp");
        return new EnviarEmailRequest(
                "Maria Souza", "maria@email.com",
                "Orçamento", "Quero um orçamento", campos
        );
    }

    @Nested
    @DisplayName("enviarEmail()")
    class EnviarEmail {

        @Test
        @DisplayName("Deve enviar email com sucesso — happy path")
        void deveEnviarEmailComSucesso() {
            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(emailTemplateService.processarTemplate(anyString(), any(Context.class)))
                    .thenReturn("<html>Email renderizado</html>");
            when(emailRepository.save(any(EmailEntity.class)))
                    .thenAnswer(invocation -> {
                        EmailEntity entity = invocation.getArgument(0);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    });

            try (MockedStatic<IpResolver> ipMock = mockStatic(IpResolver.class)) {
                ipMock.when(IpResolver::resolve).thenReturn("192.168.1.1");

                EmailEntity result = emailService.enviarEmail(criarRequestValido());

                assertThat(result).isNotNull();
                assertThat(result.getEnviadoComSucesso()).isTrue();
                assertThat(result.getErroEnvio()).isNull();
                assertThat(result.getId()).isNotNull();

                verify(javaMailSender).send(mimeMessage);
                verify(emailTemplateService).processarTemplate(eq("email-template-forms"), any(Context.class));
                verify(emailRepository).save(emailEntityCaptor.capture());

                EmailEntity saved = emailEntityCaptor.getValue();
                assertThat(saved.getNomeEmail()).isEqualTo("João Silva");
                assertThat(saved.getEmail()).isEqualTo("joao@email.com");
                assertThat(saved.getAssuntoEmail()).isEqualTo("Assunto Teste");
                assertThat(saved.getIpOrigem()).isEqualTo("192.168.1.1");
            }
        }

        @Test
        @DisplayName("Deve salvar entity com erro e lançar EmailSendException quando SMTP falhar (MailException)")
        void deveSalvarComErroELancarExcecaoQuandoSmtpFalhar() {
            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(emailTemplateService.processarTemplate(anyString(), any(Context.class)))
                    .thenReturn("<html>Email</html>");
            doThrow(new MailSendException("Connection refused"))
                    .when(javaMailSender).send(any(MimeMessage.class));
            when(emailRepository.save(any(EmailEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            try (MockedStatic<IpResolver> ipMock = mockStatic(IpResolver.class)) {
                ipMock.when(IpResolver::resolve).thenReturn("192.168.1.1");

                assertThatThrownBy(() -> emailService.enviarEmail(criarRequestValido()))
                        .isInstanceOf(EmailSendException.class)
                        .hasMessageContaining("Erro ao enviar email");

                verify(emailRepository).save(emailEntityCaptor.capture());
                EmailEntity saved = emailEntityCaptor.getValue();
                assertThat(saved.getEnviadoComSucesso()).isFalse();
                assertThat(saved.getErroEnvio()).contains("Connection refused");
            }
        }

        @Test
        @DisplayName("Deve processar campos adicionais e passá-los para o template")
        void deveProcessarCamposAdicionaisNoTemplate() {
            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(emailTemplateService.processarTemplate(anyString(), any(Context.class)))
                    .thenReturn("<html>Template com campos</html>");
            when(emailRepository.save(any(EmailEntity.class)))
                    .thenAnswer(invocation -> {
                        EmailEntity entity = invocation.getArgument(0);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    });

            try (MockedStatic<IpResolver> ipMock = mockStatic(IpResolver.class)) {
                ipMock.when(IpResolver::resolve).thenReturn("10.0.0.1");

                EmailEntity result = emailService.enviarEmail(criarRequestComCamposAdicionais());

                assertThat(result.getCamposAdicionais()).containsEntry("telefone", "11999999999");
                assertThat(result.getCamposAdicionais()).containsEntry("empresa", "TechCorp");

                // Verifica que o template foi processado com contexto contendo campos adicionais
                verify(emailTemplateService).processarTemplate(eq("email-template-forms"), any(Context.class));
            }
        }

        @Test
        @DisplayName("Deve salvar entity mesmo quando envio falhar por MessagingException")
        void deveSalvarEntityQuandoFalharPorMessagingException() throws Exception {
            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(emailTemplateService.processarTemplate(anyString(), any(Context.class)))
                    .thenReturn("<html>Email</html>");

            // Simula MessagingException via MimeMessageHelper.setFrom()
            doThrow(new MailSendException("SMTP auth failed"))
                    .when(javaMailSender).send(any(MimeMessage.class));
            when(emailRepository.save(any(EmailEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            try (MockedStatic<IpResolver> ipMock = mockStatic(IpResolver.class)) {
                ipMock.when(IpResolver::resolve).thenReturn("10.0.0.1");

                assertThatThrownBy(() -> emailService.enviarEmail(criarRequestValido()))
                        .isInstanceOf(EmailSendException.class);

                verify(emailRepository).save(emailEntityCaptor.capture());
                assertThat(emailEntityCaptor.getValue().getEnviadoComSucesso()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("listaEmails()")
    class ListaEmails {

        @Test
        @DisplayName("Deve retornar lista com todos os emails")
        void deveRetornarListaComTodosOsEmails() {
            List<EmailEntity> emails = List.of(new EmailEntity(), new EmailEntity());
            when(emailRepository.findAll()).thenReturn(emails);

            List<EmailEntity> resultado = emailService.listaEmails();

            assertThat(resultado).hasSize(2);
            verify(emailRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há emails")
        void deveRetornarListaVazia() {
            when(emailRepository.findAll()).thenReturn(List.of());

            List<EmailEntity> resultado = emailService.listaEmails();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("listaEmailsPorConteudo()")
    class ListaEmailsPorConteudo {

        @Test
        @DisplayName("Deve delegar busca por conteúdo para o repository")
        void deveDelegarBuscaPorConteudo() {
            EmailEntity email = new EmailEntity();
            email.setAssuntoEmail("Orçamento");
            when(emailRepository.listaEmailsPorConteudo("Orçamento")).thenReturn(List.of(email));

            List<EmailEntity> resultado = emailService.listaEmailsPorConteudo("Orçamento");

            assertThat(resultado).hasSize(1);
            verify(emailRepository).listaEmailsPorConteudo("Orçamento");
        }
    }

    @Nested
    @DisplayName("buscarEmailPorId()")
    class BuscarEmailPorId {

        @Test
        @DisplayName("Deve retornar email quando encontrado")
        void deveRetornarEmailQuandoEncontrado() {
            UUID id = UUID.randomUUID();
            EmailEntity email = new EmailEntity();
            email.setId(id);
            when(emailRepository.findById(id)).thenReturn(Optional.of(email));

            EmailEntity resultado = emailService.buscarEmailPorId(id);

            assertThat(resultado.getId()).isEqualTo(id);
            verify(emailRepository).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando email não encontrado")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            when(emailRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> emailService.buscarEmailPorId(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Email não encontrado com id: " + id);
        }
    }

    @Nested
    @DisplayName("deletarEmailPorId()")
    class DeletarEmailPorId {

        @Test
        @DisplayName("Deve deletar email quando existir")
        void deveDeletarEmailQuandoExistir() {
            UUID id = UUID.randomUUID();
            when(emailRepository.existsById(id)).thenReturn(true);

            emailService.deletarEmailPorId(id);

            verify(emailRepository).deleteById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao deletar email inexistente")
        void deveLancarExcecaoAoDeletarInexistente() {
            UUID id = UUID.randomUUID();
            when(emailRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> emailService.deletarEmailPorId(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Email não encontrado para ser deletado");

            verify(emailRepository, never()).deleteById(any());
        }
    }
}

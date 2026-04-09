package com.alexsantosportfolio.emailcontactservice.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.alexsantosportfolio.emailcontactservice.dto.request.EnviarEmailRequest;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.exception.EmailSendException;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;
import com.alexsantosportfolio.emailcontactservice.util.IpResolver;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    @NonNull
    private String meuEmail;

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final EmailTemplateService emailTemplateService;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender,
                        EmailTemplateService emailTemplateService) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
        this.emailTemplateService = emailTemplateService;
    }

    @Transactional
    public EmailEntity enviarEmail(EnviarEmailRequest request) {

        EmailEntity emailEntity = new EmailEntity(
                request.nomeEmail(),
                request.email(),
                request.assuntoEmail(),
                request.mensagemEmail(),
                false,
                null,
                IpResolver.resolve()
        );

        try {
            // Cria contexto do Thymeleaf
            Context context = new Context();
            context.setVariable("nome", request.nomeEmail());
            context.setVariable("email", request.email());
            context.setVariable("assunto", request.assuntoEmail());
            context.setVariable("mensagem", request.mensagemEmail());
            context.setVariable("data", LocalDateTime.now());

            // Renderizar HTML
            String html = emailTemplateService.processarTemplate("email-template-forms", context);

            // Criar email MIME
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(request.email());
            helper.setTo(meuEmail);
            helper.setSubject(request.assuntoEmail());
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);

            emailEntity.setEnviadoComSucesso(true);
            logger.info("Email enviado com sucesso de: {}", request.email());

        } catch (MailException | jakarta.mail.MessagingException e) {
            // salva no banco MESMO ASSIM
            emailEntity.setEnviadoComSucesso(false);
            emailEntity.setErroEnvio(e.getMessage());
            emailRepository.save(emailEntity);

            logger.error("Erro ao enviar email de: {}", request.email(), e);

            // dispara exceção de domínio para o handler
            throw new EmailSendException("Erro ao enviar email", e);
        }

        return emailRepository.save(emailEntity);
    }

    @Transactional(readOnly = true)
    public List<EmailEntity> listaEmails() {
        return emailRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EmailEntity> listaEmailsPorConteudo(String termo) {
        return emailRepository.listaEmailsPorConteudo(termo);
    }

    @Transactional(readOnly = true)
    public EmailEntity buscarEmailPorId(@NonNull UUID id) {
        return emailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Email não encontrado com id: " + id));
    }

    @Transactional
    public void deletarEmailPorId(@NonNull UUID id) {
        if (!emailRepository.existsById(id)) {
            throw new EntityNotFoundException("Email não encontrado para ser deletado");
        }
        emailRepository.deleteById(id);
    }
}
package com.alexsantosportfolio.emailcontactservice.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;

import com.alexsantosportfolio.emailcontactservice.DTO.EnviarEmailDTO;
import com.alexsantosportfolio.emailcontactservice.config.EmailSendException;
import com.alexsantosportfolio.emailcontactservice.config.ObterIpUsuario;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String meuEmail;

    private final JavaMailSender javaMailSender;

    private final EmailRepository emailRepository;

    private final EmailTemplateService emailTemplateService;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender, EmailTemplateService emailTemplateService) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
        this.emailTemplateService = emailTemplateService;
    }

    public EmailEntity enviarEmail(EnviarEmailDTO emailDTO) {

        EmailEntity emailEntity = new EmailEntity(
                emailDTO.nomeEmail(),
                emailDTO.email(),
                emailDTO.assuntoEmail(),
                emailDTO.mensagemEmail(),
                false,
                null,
                ObterIpUsuario.obterIp()
        );
    
        try {

            // 1️⃣ Criar contexto do Thymeleaf
            Context context = new Context();
            context.setVariable("nome", emailDTO.nomeEmail());
            context.setVariable("email", emailDTO.email());
            context.setVariable("assunto", emailDTO.assuntoEmail());
            context.setVariable("mensagem", emailDTO.mensagemEmail());
            context.setVariable("data", LocalDateTime.now());

            // 2️⃣ Renderizar HTML
            String html = emailTemplateService.processarTemplate("email-template-forms", context);

            // 3️⃣ Criar email MIME
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(emailDTO.email());
            helper.setTo(meuEmail);
            helper.setSubject(emailDTO.assuntoEmail());
            helper.setText(html, true); // true = HTML

            javaMailSender.send(mimeMessage);

            emailEntity.setEnviadoComSucesso(true);
    
        } catch (MailException | jakarta.mail.MessagingException e) {
    
            // salva no banco MESMO ASSIM
            emailEntity.setEnviadoComSucesso(false);
            emailEntity.setErroEnvio(e.getMessage());
            emailRepository.save(emailEntity);
    
            // dispara exceção de domínio para o handler
            throw new EmailSendException("Erro ao enviar email", e);
        }
    
        // retorna salvo
        return emailRepository.save(emailEntity);
    }
    
    

    public List<EmailEntity> listaEmails() {
        return emailRepository.findAll();
    }

    public List<EmailEntity> listaEmailsPorConteudo(String termo) {
        return emailRepository.listaEmailsPorConteudo(termo);
    }

    public EmailEntity buscarEmailPorId(@NonNull UUID id) {
        return emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));
    }

    public void deletarEmailPorId(@NonNull UUID id) {
        try {
            emailRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Email não encontrado para ser deletado");
        }
    }
}
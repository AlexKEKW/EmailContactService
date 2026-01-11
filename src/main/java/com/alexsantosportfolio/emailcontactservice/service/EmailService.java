package com.alexsantosportfolio.emailcontactservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.alexsantosportfolio.emailcontactservice.DTO.EnviarEmailDTO;
import com.alexsantosportfolio.emailcontactservice.config.EmailSendException;
import com.alexsantosportfolio.emailcontactservice.config.ObterIpUsuario;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String meuEmail;

    private final JavaMailSender javaMailSender;

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
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
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailDTO.email());
            mailMessage.setTo(meuEmail);
            mailMessage.setSubject(emailDTO.assuntoEmail());
            mailMessage.setText(
                    "Nome: " + emailDTO.nomeEmail() + "\n" +
                    "E-mail: " + emailDTO.email() + "\n\n" +
                    "Mensagem:\n" + emailDTO.mensagemEmail()
            );
    
            javaMailSender.send(mailMessage);
    
            emailEntity.setEnviadoComSucesso(true);
    
        } catch (MailException e) {
    
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
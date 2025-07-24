package com.alexsantosportfolio.emailcontactservice.service;

import com.alexsantosportfolio.emailcontactservice.DTO.EnviarEmailDTO;
import com.alexsantosportfolio.emailcontactservice.config.ObterIpUsuario;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.repository.EmailRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public UUID enviarEmail(EnviarEmailDTO emailDTO) {

        EmailEntity emailEntity = new EmailEntity(
                emailDTO.nomeEmail(),
                emailDTO.email(),
                emailDTO.assuntoEmail(),
                emailDTO.mensagemEmail(),
                LocalDateTime.now(),
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

            emailEntity.setDataEnvio(LocalDateTime.now());
            emailEntity.setEnviadoComSucesso(true);

        } catch (Exception e) {
            emailEntity.setEnviadoComSucesso(false);
            emailEntity.setErroEnvio(e.getMessage());
        }

        return emailRepository.save(emailEntity).getId();
    }

    public List<EmailEntity> listaEmails() {
        return emailRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EmailEntity> listaEmailsPorConteudo(String termo) {
        return emailRepository.listaEmailsPorConteudo(termo);
    }
}

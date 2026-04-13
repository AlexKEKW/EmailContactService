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

/**
 * Serviço responsável pelo envio, persistência e consulta de emails de contato.
 *
 * <p>Processa requisições do formulário frontend, renderiza o template HTML
 * via Thymeleaf (incluindo campos dinâmicos), envia via SMTP e persiste
 * o registro completo no banco de dados.</p>
 */
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

    /**
     * Envia um email de contato a partir dos dados do formulário e persiste o registro.
     *
     * <p>O método realiza as seguintes etapas:</p>
     * <ol>
     *   <li>Cria a entidade com os campos fixos e dinâmicos do formulário</li>
     *   <li>Monta o contexto Thymeleaf com todos os dados, incluindo {@code camposAdicionais}</li>
     *   <li>Renderiza o template HTML com os campos dinâmicos iterados</li>
     *   <li>Envia o email via SMTP</li>
     *   <li>Persiste a entidade no banco de dados (mesmo em caso de falha no envio)</li>
     * </ol>
     *
     * @param request DTO contendo os dados fixos e dinâmicos do formulário
     * @return entidade persistida com o status do envio
     * @throws EmailSendException se ocorrer falha no envio via SMTP
     */
    @Transactional
    public EmailEntity enviarEmail(EnviarEmailRequest request) {

        EmailEntity emailEntity = new EmailEntity(
                request.nomeEmail(),
                request.email(),
                request.assuntoEmail(),
                request.mensagemEmail(),
                false,
                null,
                IpResolver.resolve(),
                request.camposAdicionais()
        );

        try {
            // Cria contexto do Thymeleaf
            Context context = new Context();
            context.setVariable("nome", request.nomeEmail());
            context.setVariable("email", request.email());
            context.setVariable("assunto", request.assuntoEmail());
            context.setVariable("mensagem", request.mensagemEmail());
            context.setVariable("data", LocalDateTime.now());
            context.setVariable("camposAdicionais", request.camposAdicionais());

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

    /**
     * Retorna todos os registros de email persistidos.
     *
     * @return lista de entidades de email
     */
    @Transactional(readOnly = true)
    public List<EmailEntity> listaEmails() {
        return emailRepository.findAll();
    }

    /**
     * Busca emails cujo assunto ou mensagem contenham o termo informado.
     *
     * @param termo texto para busca parcial
     * @return lista de entidades que correspondem ao filtro
     */
    @Transactional(readOnly = true)
    public List<EmailEntity> listaEmailsPorConteudo(String termo) {
        return emailRepository.listaEmailsPorConteudo(termo);
    }

    /**
     * Busca um email específico pelo seu identificador UUID.
     *
     * @param id identificador único do registro
     * @return entidade do email encontrado
     * @throws EntityNotFoundException se nenhum email for encontrado com o ID informado
     */
    @Transactional(readOnly = true)
    public EmailEntity buscarEmailPorId(@NonNull UUID id) {
        return emailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Email não encontrado com id: " + id));
    }

    /**
     * Remove permanentemente um registro de email do banco de dados.
     *
     * @param id identificador único do registro a ser deletado
     * @throws EntityNotFoundException se nenhum email for encontrado com o ID informado
     */
    @Transactional
    public void deletarEmailPorId(@NonNull UUID id) {
        if (!emailRepository.existsById(id)) {
            throw new EntityNotFoundException("Email não encontrado para ser deletado");
        }
        emailRepository.deleteById(id);
    }
}
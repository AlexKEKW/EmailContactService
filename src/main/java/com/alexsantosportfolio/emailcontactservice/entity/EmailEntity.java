package com.alexsantosportfolio.emailcontactservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_emails")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome_email", nullable = false, length = 100)
    private String nomeEmail;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Assunto é obrigatório")
    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    @Column(name = "assunto_email", nullable = false, length = 200)
    private String assuntoEmail;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 2000, message = "Mensagem deve ter no máximo 2000 caracteres")
    @Column(name = "mensagem_email", nullable = false, length = 2000)
    private String mensagemEmail;

    @CreationTimestamp
    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "enviado_com_sucesso", nullable = false)
    private Boolean enviadoComSucesso = false;

    @Size(max = 500, message = "Erro deve ter no máximo 500 caracteres")
    @Column(name = "erro_envio", length = 500)
    private String erroEnvio;

    @Column(name = "ip_origem")
    private String ipOrigem;

    public EmailEntity() {
    }

    public EmailEntity(String nomeEmail, String email, String assuntoEmail, String mensagemEmail, Boolean enviadoComSucesso, String erroEnvio, String ipOrigem) {
        this.nomeEmail = nomeEmail;
        this.email = email;
        this.assuntoEmail = assuntoEmail;
        this.mensagemEmail = mensagemEmail;
        this.enviadoComSucesso = enviadoComSucesso;
        this.erroEnvio = erroEnvio;
        this.ipOrigem = ipOrigem;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeEmail() {
        return nomeEmail;
    }

    public void setNomeEmail(String nomeEmail) {
        this.nomeEmail = nomeEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssuntoEmail() {
        return assuntoEmail;
    }

    public void setAssuntoEmail(String assuntoEmail) {
        this.assuntoEmail = assuntoEmail;
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Boolean getEnviadoComSucesso() {
        return enviadoComSucesso;
    }

    public void setEnviadoComSucesso(Boolean enviadoComSucesso) {
        this.enviadoComSucesso = enviadoComSucesso;
    }

    public String getErroEnvio() {
        return erroEnvio;
    }

    public void setErroEnvio(String erroEnvio) {
        this.erroEnvio = erroEnvio;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }
}

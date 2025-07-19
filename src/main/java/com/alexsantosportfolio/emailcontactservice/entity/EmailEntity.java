package com.alexsantosportfolio.emailcontactservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_emails")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nomeEmail")
    private String nomeEmail;

    @Column(name = "email")
    private String email;

    @Column(name = "assuntoEmail")
    private String assuntoEmail;

    @Column(name = "mensagemEmail")
    private String mensagemEmail;

    @Column(name = "dataEnvio")
    private LocalDateTime dataEnvio;

    @Column(name = "enviadoComSucesso")
    private Boolean enviadoComSucesso = false;

    @Column(name = "erroEnvio")
    private String erroEnvio;

    @Column(name = "ipOrigem")
    private String ipOrigem;

    public EmailEntity() {
    }

    public EmailEntity(UUID id, String nomeEmail, String email, String assuntoEmail, String mensagemEmail, LocalDateTime dataEnvio, Boolean enviadoComSucesso, String erroEnvio, String ipOrigem) {
        this.id = id;
        this.nomeEmail = nomeEmail;
        this.email = email;
        this.assuntoEmail = assuntoEmail;
        this.mensagemEmail = mensagemEmail;
        this.dataEnvio = dataEnvio;
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

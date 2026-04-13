package com.alexsantosportfolio.emailcontactservice.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um registro de email de contato enviado
 * através do formulário frontend.
 *
 * <p>Além dos campos fixos (nome, email, assunto, mensagem), suporta
 * campos dinâmicos através de {@link #camposAdicionais}, armazenados
 * como JSONB no PostgreSQL via mapeamento nativo do Hibernate 6.</p>
 *
 * @see org.hibernate.annotations.JdbcTypeCode
 */
@Entity
@Table(name = "tb_emails")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_email", nullable = false, length = 100)
    private String nomeEmail;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "assunto_email", nullable = false, length = 200)
    private String assuntoEmail;

    @Column(name = "mensagem_email", nullable = false, length = 2000)
    private String mensagemEmail;

    @CreationTimestamp
    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "enviado_com_sucesso", nullable = false)
    private Boolean enviadoComSucesso = false;

    @Column(name = "erro_envio", length = 500)
    private String erroEnvio;

    @Column(name = "ip_origem")
    private String ipOrigem;

    /**
     * Campos extras enviados pelo formulário frontend que não possuem
     * mapeamento fixo na entidade. Armazenados como JSONB no PostgreSQL,
     * permitindo flexibilidade total nos formulários de contato.
     *
     * <p>Exemplo de conteúdo: {@code {"telefone": "11999999999", "empresa": "TechCorp"}}</p>
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "campos_adicionais", columnDefinition = "jsonb")
    private Map<String, Object> camposAdicionais;

    public EmailEntity() {
    }

    public EmailEntity(String nomeEmail, String email, String assuntoEmail, String mensagemEmail,
                       Boolean enviadoComSucesso, String erroEnvio, String ipOrigem,
                       Map<String, Object> camposAdicionais) {
        this.nomeEmail = nomeEmail;
        this.email = email;
        this.assuntoEmail = assuntoEmail;
        this.mensagemEmail = mensagemEmail;
        this.enviadoComSucesso = enviadoComSucesso;
        this.erroEnvio = erroEnvio;
        this.ipOrigem = ipOrigem;
        this.camposAdicionais = camposAdicionais;
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

    public Map<String, Object> getCamposAdicionais() {
        return camposAdicionais;
    }

    public void setCamposAdicionais(Map<String, Object> camposAdicionais) {
        this.camposAdicionais = camposAdicionais;
    }
}

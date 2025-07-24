package com.alexsantosportfolio.emailcontactservice.DTO;

public record EnviarEmailDTO(
        String nomeEmail,
        String email,
        String assuntoEmail,
        String mensagemEmail
) {}

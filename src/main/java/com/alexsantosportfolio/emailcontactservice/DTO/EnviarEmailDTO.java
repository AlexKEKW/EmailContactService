package com.alexsantosportfolio.emailcontactservice.DTO;

import org.springframework.lang.NonNull;

public record EnviarEmailDTO(
        @NonNull String nomeEmail,
        @NonNull String email,
        @NonNull String assuntoEmail,
        @NonNull String mensagemEmail
) {}

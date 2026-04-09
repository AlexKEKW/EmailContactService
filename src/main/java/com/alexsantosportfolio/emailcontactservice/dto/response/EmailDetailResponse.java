package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;

public record EmailDetailResponse(
        UUID id,
        String nomeEmail,
        String email,
        String assuntoEmail,
        String mensagemEmail,
        LocalDateTime dataEnvio,
        boolean enviadoComSucesso
) {
    public static EmailDetailResponse from(EmailEntity entity) {
        return new EmailDetailResponse(
                entity.getId(),
                entity.getNomeEmail(),
                entity.getEmail(),
                entity.getAssuntoEmail(),
                entity.getMensagemEmail(),
                entity.getDataEnvio(),
                entity.getEnviadoComSucesso()
        );
    }
}

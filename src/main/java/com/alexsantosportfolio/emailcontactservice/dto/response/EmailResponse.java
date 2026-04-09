package com.alexsantosportfolio.emailcontactservice.dto.response;

import java.time.LocalDateTime;

public record EmailResponse(
        boolean success,
        String message,
        LocalDateTime timestamp
) {
    public static EmailResponse success(String message) {
        return new EmailResponse(true, message, LocalDateTime.now());
    }
}

package com.alexsantosportfolio.emailcontactservice.DTO;

import java.time.LocalDateTime;

public record EmailResponseDTO(
   Boolean success,
   String message,
   LocalDateTime timestamp
) {}

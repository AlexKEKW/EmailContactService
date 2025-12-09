package com.alexsantosportfolio.emailcontactservice.config;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

import com.alexsantosportfolio.emailcontactservice.DTO.EmailResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 — Erro de validação (bean validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EmailResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Erro de validação: {}", mensagem);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new EmailResponseDTO(false, "Erro de validação: " + mensagem, LocalDateTime.now()));
    }

    // 404 — Recurso não encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<EmailResponseDTO> handleEntityNotFound(EntityNotFoundException e) {
        logger.warn("Recurso não encontrado: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new EmailResponseDTO(false, e.getMessage(), LocalDateTime.now()));
    }

    // 400 — erros de regra de negócio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<EmailResponseDTO> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Erro de argumento inválido: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new EmailResponseDTO(false, e.getMessage(), LocalDateTime.now()));
    }

    // 500 — erros inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmailResponseDTO> handleUnexpected(Exception e) {
        logger.error("Erro inesperado", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new EmailResponseDTO(false, "Erro interno no servidor.", LocalDateTime.now()));
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<EmailResponseDTO> handleEmailSendError(EmailSendException e) {
        logger.error("Falha ao enviar email", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new EmailResponseDTO(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }


}
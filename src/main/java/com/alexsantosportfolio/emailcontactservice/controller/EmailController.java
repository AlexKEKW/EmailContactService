package com.alexsantosportfolio.emailcontactservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alexsantosportfolio.emailcontactservice.dto.request.EnviarEmailRequest;
import com.alexsantosportfolio.emailcontactservice.dto.response.EmailDetailResponse;
import com.alexsantosportfolio.emailcontactservice.dto.response.EmailResponse;
import com.alexsantosportfolio.emailcontactservice.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/contato")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EmailResponse> enviarEmail(@RequestBody @Valid EnviarEmailRequest request) {
        var emailEntity = emailService.enviarEmail(request);

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(emailEntity.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(EmailResponse.success("Email enviado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<List<EmailDetailResponse>> listaEmails() {
        var emails = emailService.listaEmails().stream()
                .map(EmailDetailResponse::from)
                .toList();

        return ResponseEntity.ok(emails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailDetailResponse> buscarEmailPorId(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(
                EmailDetailResponse.from(emailService.buscarEmailPorId(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmailPorId(@PathVariable @NonNull UUID id) {
        emailService.deletarEmailPorId(id);
        return ResponseEntity.noContent().build();
    }
}

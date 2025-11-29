package com.alexsantosportfolio.emailcontactservice.controller;

import com.alexsantosportfolio.emailcontactservice.DTO.EnviarEmailDTO;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/contato")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> enviarEmail(@RequestBody @Valid EnviarEmailDTO emailDTO) {
        try {
            var emailId = emailService.enviarEmail(emailDTO);
            return ResponseEntity.created(URI.create("/v1/contato/" + emailId.toString())).build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EmailEntity>> listaEmails() {
        return ResponseEntity.ok(emailService.listaEmails());
    }

}

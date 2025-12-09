package com.alexsantosportfolio.emailcontactservice.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alexsantosportfolio.emailcontactservice.DTO.EmailResponseDTO;
import com.alexsantosportfolio.emailcontactservice.DTO.EnviarEmailDTO;
import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import com.alexsantosportfolio.emailcontactservice.service.EmailService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/contato")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EmailResponseDTO> enviarEmail(@RequestBody @Valid EnviarEmailDTO emailDTO) {

        var emailEntity = emailService.enviarEmail(emailDTO);

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(emailEntity.getId())
                .toUri();

        EmailResponseDTO response = new EmailResponseDTO(
                true,
                "Email enviado com sucesso",
                LocalDateTime.now()
        );

        return ResponseEntity.created(location).body(response);
    }


    @GetMapping
    public ResponseEntity<List<EmailEntity>> listaEmails() {
        return ResponseEntity.ok(emailService.listaEmails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailEntity> buscarEmailPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(emailService.buscarEmailPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmailPorId(@PathVariable UUID id) {
        emailService.deletarEmailPorId(id);

        return ResponseEntity.noContent().build(); // 204
    }

}

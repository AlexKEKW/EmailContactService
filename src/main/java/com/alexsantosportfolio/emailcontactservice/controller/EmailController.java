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
import com.alexsantosportfolio.emailcontactservice.dto.response.ErrorResponse;
import com.alexsantosportfolio.emailcontactservice.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/contato")
@Tag(name = "Contato", description = "Endpoints para envio e gerenciamento de emails de contato")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(
            summary = "Enviar email de contato",
            description = "Recebe os dados do formulário de contato, envia o email via SMTP "
                    + "e persiste o registro no banco de dados. Retorna o header Location "
                    + "com a URI do recurso criado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Email enviado e registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class)),
                    headers = @Header(
                            name = "Location",
                            description = "URI do registro criado",
                            schema = @Schema(type = "string", example = "/v1/contato/f1b8f24a-7a68-4c16-9fd7-11d1b4d2ef9c"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos (campos obrigatórios ausentes, email com formato inválido, etc.)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Falha ao enviar email pelo servidor SMTP",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
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

    @Operation(
            summary = "Listar todos os emails",
            description = "Retorna a lista completa de emails de contato registrados, "
                    + "ordenados por data de envio. Não expõe campos internos como IP ou mensagens de erro."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de emails retornada com sucesso",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = EmailDetailResponse.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<EmailDetailResponse>> listaEmails() {
        var emails = emailService.listaEmails().stream()
                .map(EmailDetailResponse::from)
                .toList();

        return ResponseEntity.ok(emails);
    }

    @Operation(
            summary = "Buscar email por ID",
            description = "Retorna os detalhes de um email de contato específico pelo seu identificador UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = EmailDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email não encontrado com o ID informado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmailDetailResponse> buscarEmailPorId(
            @Parameter(description = "UUID do registro de email", example = "f1b8f24a-7a68-4c16-9fd7-11d1b4d2ef9c",
                    required = true)
            @PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(
                EmailDetailResponse.from(emailService.buscarEmailPorId(id))
        );
    }

    @Operation(
            summary = "Deletar email por ID",
            description = "Remove permanentemente um registro de email de contato do banco de dados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Email deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email não encontrado com o ID informado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmailPorId(
            @Parameter(description = "UUID do registro de email a ser deletado",
                    example = "f1b8f24a-7a68-4c16-9fd7-11d1b4d2ef9c", required = true)
            @PathVariable @NonNull UUID id) {
        emailService.deletarEmailPorId(id);
        return ResponseEntity.noContent().build();
    }
}

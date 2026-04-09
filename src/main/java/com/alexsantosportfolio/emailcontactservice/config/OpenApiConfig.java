package com.alexsantosportfolio.emailcontactservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI emailContactServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Email Contact Service API")
                        .description("API REST para envio e gerenciamento de emails de contato. "
                                + "Permite enviar emails via formulário, listar histórico de envios "
                                + "e gerenciar registros de contato.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alex Santos")
                                .url("https://github.com/AlexKEKW"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento")));
    }
}

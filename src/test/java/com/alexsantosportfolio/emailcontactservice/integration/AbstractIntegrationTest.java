package com.alexsantosportfolio.emailcontactservice.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Classe base para testes de integração.
 * Configuração adaptada para usar H2 in-memory com compatibilidade PostgreSQL,
 * visto que o ambiente Docker via Testcontainers não está disponível.
 */
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        // Removemos o limite de Testcontainers e deixamos o Hibernate descobrir o dialeto apropriado (H2Dialect ou compativel)
    }
}

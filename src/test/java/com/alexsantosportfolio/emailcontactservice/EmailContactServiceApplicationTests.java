package com.alexsantosportfolio.emailcontactservice;

import com.alexsantosportfolio.emailcontactservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@DisplayName("ApplicationContext — smoke test")
class EmailContactServiceApplicationTests extends AbstractIntegrationTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("O contexto Spring deve carregar com sucesso")
    void contextLoads() {
        // Smoke test: verifica que toda a configuração Spring carrega sem erros
    }
}

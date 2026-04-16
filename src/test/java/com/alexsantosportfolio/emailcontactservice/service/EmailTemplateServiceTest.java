package com.alexsantosportfolio.emailcontactservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailTemplateService — processamento de templates")
class EmailTemplateServiceTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    @Test
    @DisplayName("Deve delegar processamento para o TemplateEngine com os argumentos corretos")
    void deveDelegarProcessamentoParaTemplateEngine() {
        String templateName = "email-template-forms";
        Context context = new Context();
        context.setVariable("nome", "João");
        String htmlEsperado = "<html><body>Olá João</body></html>";

        when(templateEngine.process(templateName, context)).thenReturn(htmlEsperado);

        String resultado = emailTemplateService.processarTemplate(templateName, context);

        assertThat(resultado).isEqualTo(htmlEsperado);
        verify(templateEngine).process(templateName, context);
    }
}

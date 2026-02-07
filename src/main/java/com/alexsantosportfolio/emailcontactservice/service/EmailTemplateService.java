package com.alexsantosportfolio.emailcontactservice.service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String processarTemplate(String template, Context context) {
        return templateEngine.process(template, context);
    }

}


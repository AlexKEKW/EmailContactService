Analisando o repositório **EmailContactService** na branch `developer`, vejo que o projeto é um **Spring Boot 3.5.3 com Java 21**, utilizando **Flyway** para migrations de banco de dados (PostgreSQL/H2) e validação de dados. Porém, o projeto está em estágio inicial com apenas a classe principal criada.

Com base nas **melhores práticas de qualidade de software e escalabilidade**, aqui estão as estruturas/arquitetura que você deveria implementar:

## 📐 **Arquitetura Recomendada (Clean Architecture + Layered)**

```
src/main/java/com/alexsantosportfolio/emailcontactservice/
├── config/                          # Configurações da aplicação
│   ├── DatabaseConfig.java
│   ├── EmailConfig.java
│   └── SecurityConfig.java
│
├── controller/                      # Camada de Apresentação (REST)
│   ├── dto/
│   │   ├── request/
│   │   │   └── ContactEmailRequest.java
│   │   └── response/
│   │       ├── ContactEmailResponse.java
│   │       └── ErrorResponse.java
│   └── ContactEmailController.java
│
├── service/                         # Camada de Lógica de Negócio
│   ├── ContactEmailService.java
│   ├── EmailSenderService.java
│   └── ValidationService.java
│
├── repository/                      # Camada de Persistência
│   ├── ContactRepository.java
│   ├── EmailLogRepository.java
│   └── base/
│       └── BaseRepository.java
│
├── entity/                          # Entidades JPA/Domain
│   ├── Contact.java
│   ├── EmailLog.java
│   └── base/
│       └── BaseEntity.java
│
├── exception/                       # Tratamento de Exceções Customizadas
│   ├── BusinessException.java
│   ├── ValidationException.java
│   ├── EmailSendingException.java
│   └── GlobalExceptionHandler.java
│
├── validator/                       # Validadores Customizados
│   ├── EmailValidator.java
│   └── PhoneValidator.java
│
├── mapper/                          # DTO ↔ Entity Conversion
│   ├── ContactMapper.java
│   └── EmailLogMapper.java
│
├── util/                            # Utilitários
│   ├── Constants.java
│   ├── EmailTemplate.java
│   └── DateUtil.java
│
├── event/                           # Event-Driven Architecture (opcional, mas recomendado)
│   ├── EmailSentEvent.java
│   └── EmailEventListener.java
│
└── EmailContactServiceApplication.java
```

---

## 🛠️ **Dependências a Adicionar no pom.xml**

```xml
<!-- ORM e Banco de Dados -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Lombok (reduz boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- MapStruct (Mapeamento DTO/Entity) -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Spring Security (segurança) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Swagger/OpenAPI (documentação) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.3</version>
</dependency>

<!-- AOP (Aspect-Oriented Programming) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

---

## 📋 **Configurações Essenciais (application.yml) ou application.properties**

```yaml
spring:
  application:
    name: EmailContactService
  
  datasource:
    url: jdbc:postgresql://localhost:5432/email_contact_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate # Use Flyway para migrations
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
  
  flyway:
    locations: classpath:db/migration
    baseline-on-migrate: true
  
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000

logging:
  level:
    root: INFO
    com.alexsantosportfolio: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

server:
  servlet:
    context-path: /api
  port: 8080
```

---

## 🏗️ **Exemplos de Implementação**

### **1. Entity Base (Herança para todas as entidades)**
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean active = true;
}
```

### **2. Exception Handler Global**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
    }
}
```

### **3. Service com Logging e Transações**
```java
@Service
@Transactional
@Slf4j
public class EmailSenderService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailLogRepository emailLogRepository;
    
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            
            EmailLog log = EmailLog.builder()
                .recipient(to)
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();
            
            emailLogRepository.save(log);
            log.info("Email enviado com sucesso para: {}", to);
            
        } catch (Exception e) {
            log.error("Erro ao enviar email para: {}", to, e);
            saveErrorLog(to, e.getMessage());
        }
    }
}
```

---

## ✅ **Checklist de Melhores Práticas**

- ✅ **Separação de camadas** (Controller → Service → Repository)
- ✅ **DTOs** para entrada/saída (nunca expor entidades)
- ✅ **Validação de dados** (Bean Validation + customizados)
- ✅ **Exception handling centralizado**
- ✅ **Logging estruturado**
- ✅ **Transações gerenciadas**
- ✅ **Async/Event-driven** para operações pesadas
- ✅ **Testes unitários e integração** (JUnit 5 + Mockito)
- ✅ **Documentação com Swagger/OpenAPI**
- ✅ **Versionamento de API** (/api/v1/)
- ✅ **Migrations com Flyway**
- ✅ **Configuração externalizada** (application.yml)
- ✅ **Security (Spring Security)**
- ✅ **Cache** (se necessário)
- ✅ **Monitoring e Observability** (Actuator)

---

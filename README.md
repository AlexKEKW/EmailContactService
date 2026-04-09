# 📧 Email Contact Service

API REST para envio e gerenciamento de emails de contato, construída com Spring Boot 3.5 e Java 21. Ideal para integrar com formulários de contato em portfólios e sites pessoais.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

---

## ✨ Funcionalidades

- **Envio de email** via SMTP com template HTML responsivo (Thymeleaf)
- **Persistência** de todos os emails enviados no banco de dados
- **Histórico** — listagem e busca de emails por ID
- **Validação de dados** com Bean Validation (Jakarta)
- **Tratamento global de exceções** com respostas padronizadas
- **Documentação interativa** via Swagger UI (OpenAPI 3)
- **Migrations** automáticas com Flyway
- **Captura de IP** do remetente (suporte a proxy/X-Forwarded-For)

---

## 🛠️ Tech Stack

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.5.3 |
| **Banco de Dados** | PostgreSQL 17 (Docker) |
| **ORM** | Spring Data JPA / Hibernate |
| **Migrations** | Flyway |
| **Template de Email** | Thymeleaf |
| **Validação** | Jakarta Bean Validation |
| **Documentação** | Springdoc OpenAPI 3 (Swagger UI) |
| **Build** | Maven |

---

## 📁 Estrutura do Projeto

```
src/main/java/com/alexsantosportfolio/emailcontactservice/
├── config/                  # Configurações (CORS, OpenAPI)
├── controller/              # REST Controllers
├── dto/
│   ├── request/             # DTOs de entrada (com validação)
│   └── response/            # DTOs de saída (sucesso + erro)
├── entity/                  # Entidades JPA
├── exception/               # Exceções customizadas + Handler global
├── repository/              # Repositórios Spring Data
├── service/                 # Regras de negócio
└── util/                    # Utilitários (IP resolver)
```

---

## 🚀 Como Rodar

### Pré-requisitos

- **Java 21** ([download](https://adoptium.net/))
- **Docker** e **Docker Compose** ([download](https://docs.docker.com/get-docker/))
- Conta SMTP para envio de emails (Gmail, Outlook, etc.)

### 1. Clone o repositório

```bash
git clone https://github.com/AlexKEKW/EmailContactService.git
cd EmailContactService
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env` com suas credenciais:

```env
# Banco de Dados
DB_NAME=email_service
DB_USERNAME=alex
DB_PASSWORD=sua_senha_segura

# Email (SMTP)
HOST_APP=smtp.gmail.com
PORTA_APP=587
EMAIL_APP=seu_email@gmail.com
SENHA_APP=sua_senha_de_app
PROTOCOL_APP=smtp

# CORS
CORS_ORIGINS=http://localhost:4200
```

> **Gmail:** Use uma [Senha de App](https://support.google.com/accounts/answer/185833) em vez da senha da conta.

### 3. Suba o banco de dados

```bash
docker compose --env-file .env up -d
```

### 4. Rode a aplicação

```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 📖 Documentação da API

Com a aplicação rodando, acesse:

| Recurso | URL |
|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |
| **OpenAPI YAML** | http://localhost:8080/v3/api-docs.yaml |

### Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/contato` | Enviar email de contato |
| `GET` | `/v1/contato` | Listar todos os emails |
| `GET` | `/v1/contato/{id}` | Buscar email por ID |
| `DELETE` | `/v1/contato/{id}` | Deletar email por ID |

### Exemplo — Enviar Email

```bash
curl -X POST http://localhost:8080/v1/contato \
  -H "Content-Type: application/json" \
  -d '{
    "nomeEmail": "João Silva",
    "email": "joao@email.com",
    "assuntoEmail": "Orçamento de projeto",
    "mensagemEmail": "Olá, gostaria de saber mais sobre seus serviços."
  }'
```

**Resposta (201 Created):**

```json
{
  "success": true,
  "message": "Email enviado com sucesso",
  "timestamp": "2026-04-09T01:30:00"
}
```

---

## 🗃️ Banco de Dados

O schema é gerenciado automaticamente pelo **Flyway**. As migrations ficam em `src/main/resources/db/migration/`:

| Migration | Descrição |
|---|---|
| `V1__create_emails_table.sql` | Criação da tabela `tb_emails` |
| `V2__insert_emails.sql` | Dados de seed para desenvolvimento |

### Tabela `tb_emails`

| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `nome_email` | `VARCHAR(100)` | Nome do remetente |
| `email` | `VARCHAR(255)` | Email do remetente |
| `assunto_email` | `VARCHAR(200)` | Assunto |
| `mensagem_email` | `TEXT` | Corpo da mensagem |
| `data_envio` | `TIMESTAMP` | Data/hora do envio |
| `enviado_com_sucesso` | `BOOLEAN` | Status do envio |
| `erro_envio` | `VARCHAR(500)` | Mensagem de erro (se houver) |
| `ip_origem` | `VARCHAR(45)` | IP do remetente |

---

## ⚙️ Configuração

Todas as configurações ficam em `application.properties` e são sobrescritas por variáveis de ambiente:

| Property | Variável de Ambiente | Descrição |
|---|---|---|
| `spring.datasource.url` | `DB_NAME` | Nome do banco |
| `spring.datasource.username` | `DB_USERNAME` | Usuário do banco |
| `spring.datasource.password` | `DB_PASSWORD` | Senha do banco |
| `spring.mail.host` | `HOST_APP` | Host SMTP |
| `spring.mail.port` | `PORTA_APP` | Porta SMTP |
| `spring.mail.username` | `EMAIL_APP` | Email remetente |
| `spring.mail.password` | `SENHA_APP` | Senha/App Password |
| `app.cors.allowed-origins` | `CORS_ORIGINS` | Origens CORS permitidas |

---

## 🔒 Segurança

- ✅ Credenciais externalizadas via variáveis de ambiente
- ✅ `.env` no `.gitignore` (nunca commitado)
- ✅ CORS configurável e centralizado
- ✅ Validação de entrada em todos os endpoints
- ✅ Tratamento global de exceções (sem stack traces expostos)
- ✅ Captura de IP com suporte a X-Forwarded-For

---

## 📋 Roadmap

- [ ] Testes unitários e de integração
- [ ] Rate limiting para proteção contra spam
- [ ] Paginação no endpoint de listagem
- [ ] Spring Actuator para health checks
- [ ] Profiles separados para dev/prod
- [ ] Deploy com Docker (containerizar a aplicação)
- [ ] CI/CD com GitHub Actions

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">
  Feito por <a href="https://github.com/AlexKEKW">Alex Santos</a>
</p>

CREATE TABLE IF NOT EXISTS tb_emails (
    id UUID PRIMARY KEY,
    nome_email VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    assunto_email VARCHAR(255) NOT NULL,
    mensagem_email TEXT NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    enviado_com_sucesso BOOLEAN NOT NULL,
    erro_envio VARCHAR(255),
    ip_origem VARCHAR(45)
);

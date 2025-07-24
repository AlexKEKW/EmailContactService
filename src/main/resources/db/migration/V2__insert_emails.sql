INSERT INTO tb_emails (
    id,
    nome_email,
    email,
    assunto_email,
    mensagem_email,
    data_envio,
    enviado_com_sucesso,
    erro_envio,
    ip_origem
) VALUES (
    'f1b8f24a-7a68-4c16-9fd7-11d1b4d2ef9c',
    'João Silva',
    'joao.silva@example.com',
    'Boas-vindas!',
    'Olá João, seja bem-vindo ao nosso sistema!',
    '2025-07-20T14:30:00',
    true,
    NULL,
    '192.168.1.10'
);

INSERT INTO tb_emails (
    id,
    nome_email,
    email,
    assunto_email,
    mensagem_email,
    data_envio,
    enviado_com_sucesso,
    erro_envio,
    ip_origem
) VALUES (
    'a2d5a9e1-cc27-4d08-a97b-354abf084fd0',
    'Maria Oliveira',
    'maria.oliveira@example.com',
    'Recuperação de senha',
    'Clique aqui para redefinir sua senha.',
    '2025-07-20T15:45:00',
    false,
    'SMTP server not reachable',
    '192.168.1.15'
);

INSERT INTO tb_emails (
    id,
    nome_email,
    email,
    assunto_email,
    mensagem_email,
    data_envio,
    enviado_com_sucesso,
    erro_envio,
    ip_origem
) VALUES (
    'c3ab71dc-6e64-4a1f-a865-f2136c88b2f1',
    'Carlos Mendes',
    'carlos.mendes@example.com',
    'Atualização de perfil',
    'Seu perfil foi atualizado com sucesso.',
    '2025-07-20T16:00:00',
    true,
    NULL,
    '10.0.0.1'
);

-- Adiciona coluna para armazenar campos dinâmicos do formulário em formato JSON.
-- Utiliza JSONB (Binary JSON) do PostgreSQL para indexação e consultas otimizadas.
ALTER TABLE tb_emails ADD COLUMN campos_adicionais JSONB;

COMMENT ON COLUMN tb_emails.campos_adicionais IS 'Campos extras submetidos pelo formulário do frontend, armazenados como JSON dinâmico';

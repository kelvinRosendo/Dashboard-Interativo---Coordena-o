-- V13: Adiciona campos de status e ultimo_login na tabela usuarios
-- Sprint 6.7 — Central de Usuarios e Permissoes

-- Adicionar coluna 'status' com valor padrao ATIVO para usuarios existentes
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

-- Adicionar constraint CHECK para o enum StatusUsuario
ALTER TABLE usuarios
    ADD CONSTRAINT usuarios_status_check
    CHECK (status IN ('ATIVO', 'PENDENTE', 'BLOQUEADO'));

-- Adicionar coluna 'ultimo_login' (pode ser nulo para usuarios que nunca logaram)
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS ultimo_login TIMESTAMP NULL;

-- Criar indice para buscas por status
CREATE INDEX IF NOT EXISTS idx_usuarios_status ON usuarios (status);

-- Criar indice para buscas por ultimo_login
CREATE INDEX IF NOT EXISTS idx_usuarios_ultimo_login ON usuarios (ultimo_login DESC NULLS LAST);

-- Atualizar usuarios existentes: quem ja tem perfil definido considera ATIVO
-- (usuarios ja criados via OAuth ja tiveram login, entao status = ATIVO)
UPDATE usuarios SET status = 'ATIVO' WHERE status IS NULL OR status = '';

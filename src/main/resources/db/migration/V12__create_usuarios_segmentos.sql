-- =====================================================
-- TABELA DE SEGMENTOS (normaliza o enum SegmentoCoordenacao)
-- =====================================================
CREATE TABLE segmentos (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_segmentos_slug ON segmentos(slug);
CREATE INDEX idx_segmentos_ativo ON segmentos(ativo);

-- =====================================================
-- TABELA DE USUARIOS (autenticacao via Google OAuth)
-- =====================================================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    google_id VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    foto_url TEXT,
    perfil VARCHAR(30) NOT NULL DEFAULT 'COORDENADORA',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT usuarios_perfil_check CHECK (
        perfil IN ('ADMIN', 'VICE_DIRETORA', 'COORDENADORA')
    )
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_google_id ON usuarios(google_id);
CREATE INDEX idx_usuarios_perfil ON usuarios(perfil);

-- =====================================================
-- TABELA DE ASSOCIACAO N:N USUARIO <-> SEGMENTO
-- =====================================================
CREATE TABLE usuario_segmentos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    segmento_id BIGINT NOT NULL REFERENCES segmentos(id) ON DELETE CASCADE,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_usuario_segmento UNIQUE (usuario_id, segmento_id)
);

CREATE INDEX idx_usuario_segmentos_usuario ON usuario_segmentos(usuario_id);
CREATE INDEX idx_usuario_segmentos_segmento ON usuario_segmentos(segmento_id);

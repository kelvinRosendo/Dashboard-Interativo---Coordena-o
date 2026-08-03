CREATE TABLE avisos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT,
    prioridade VARCHAR(50) NOT NULL,
    segmento VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_avisos_prioridade CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE')),
    CONSTRAINT chk_avisos_segmento CHECK (segmento IN ('EDUCACAO_INFANTIL', 'FUNDAMENTAL_1', 'FUNDAMENTAL_2', 'ENSINO_MEDIO', 'BILINGUE', 'INTEGRAL'))
);

CREATE INDEX idx_avisos_prioridade ON avisos(prioridade);
CREATE INDEX idx_avisos_segmento ON avisos(segmento);
CREATE INDEX idx_avisos_data_criacao ON avisos(data_criacao);

CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    dia_inteiro BOOLEAN NOT NULL DEFAULT true,
    segmento VARCHAR(50),
    coordenadora_id VARCHAR(255),
    google_event_id VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_eventos_segmento CHECK (segmento IN ('EDUCACAO_INFANTIL', 'FUNDAMENTAL_1', 'FUNDAMENTAL_2', 'ENSINO_MEDIO', 'BILINGUE', 'INTEGRAL'))
);

CREATE INDEX idx_eventos_data_inicio ON eventos(data_inicio);
CREATE INDEX idx_eventos_data_fim ON eventos(data_fim);
CREATE INDEX idx_eventos_segmento ON eventos(segmento);
CREATE INDEX idx_eventos_google_event_id ON eventos(google_event_id);

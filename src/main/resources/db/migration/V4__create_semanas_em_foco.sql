CREATE TABLE semanas_em_foco (
    id BIGSERIAL PRIMARY KEY,
    segmento VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT NOT NULL,
    prioridade VARCHAR(50) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT semanas_em_foco_segmento_check CHECK (
        segmento IN (
            'EDUCACAO_INFANTIL',
            'FUNDAMENTAL_1',
            'FUNDAMENTAL_2',
            'ENSINO_MEDIO'
        )
    ),
    CONSTRAINT semanas_em_foco_prioridade_check CHECK (
        prioridade IN ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE')
    )
);

CREATE INDEX idx_semanas_em_foco_segmento ON semanas_em_foco (segmento);
CREATE INDEX idx_semanas_em_foco_ativa ON semanas_em_foco (ativa);
CREATE INDEX idx_semanas_em_foco_data_inicio ON semanas_em_foco (data_inicio);

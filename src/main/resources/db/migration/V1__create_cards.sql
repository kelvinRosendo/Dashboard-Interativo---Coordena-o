CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(50) NOT NULL,
    prioridade VARCHAR(50) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_evento DATE,
    responsavel VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    observacoes TEXT,
    CONSTRAINT cards_categoria_check CHECK (
        categoria IN (
            'EVENTO',
            'FALTA_PROFESSOR',
            'SUBSTITUICAO',
            'ROTINA_ADMINISTRATIVA',
            'ROTINA_COORDENADORES',
            'SEMANA_EM_FOCO',
            'AVISO_NOTA'
        )
    ),
    CONSTRAINT cards_prioridade_check CHECK (
        prioridade IN ('BAIXA', 'MEDIA', 'ALTA')
    ),
    CONSTRAINT cards_status_check CHECK (
        status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDO')
    )
);

CREATE INDEX idx_cards_categoria ON cards (categoria);
CREATE INDEX idx_cards_status ON cards (status);
CREATE INDEX idx_cards_data_evento ON cards (data_evento);
CREATE INDEX idx_cards_data_criacao ON cards (data_criacao);

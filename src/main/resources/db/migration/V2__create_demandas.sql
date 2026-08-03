CREATE TABLE demandas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    segmento VARCHAR(50) NOT NULL,
    prioridade VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_prazo DATE,
    criada_por VARCHAR(255),
    concluida_em TIMESTAMP,
    visualizada_pela_coordenadora BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT demandas_segmento_check CHECK (
        segmento IN (
            'EDUCACAO_INFANTIL',
            'FUNDAMENTAL_1',
            'FUNDAMENTAL_2',
            'ENSINO_MEDIO'
        )
    ),
    CONSTRAINT demandas_prioridade_check CHECK (
        prioridade IN ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE')
    ),
    CONSTRAINT demandas_status_check CHECK (
        status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')
    )
);

CREATE INDEX idx_demandas_status ON demandas (status);
CREATE INDEX idx_demandas_segmento ON demandas (segmento);
CREATE INDEX idx_demandas_prioridade ON demandas (prioridade);
CREATE INDEX idx_demandas_data_criacao ON demandas (data_criacao);

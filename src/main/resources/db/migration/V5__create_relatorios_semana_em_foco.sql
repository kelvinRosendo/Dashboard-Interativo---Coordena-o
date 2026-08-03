CREATE TABLE relatorios_semana_em_foco (
    id BIGSERIAL PRIMARY KEY,
    semana_em_foco_id BIGINT NOT NULL,
    coordenadora_id VARCHAR(255) NOT NULL,
    coordenadora_nome VARCHAR(150) NOT NULL,
    coordenadora_email VARCHAR(150) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    resumo_semana TEXT,
    atividades_executadas TEXT,
    pendencias TEXT,
    observacoes TEXT,
    conclusao TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'RASCUNHO',
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    finalizado_em TIMESTAMP,
    finalizado_por VARCHAR(255),
    CONSTRAINT uk_relatorio_semana UNIQUE (semana_em_foco_id),
    CONSTRAINT fk_relatorio_semana FOREIGN KEY (semana_em_foco_id) REFERENCES semanas_em_foco(id) ON DELETE CASCADE,
    CONSTRAINT relatorios_status_check CHECK (
        status IN ('RASCUNHO', 'FINALIZADO')
    )
);

CREATE INDEX idx_relatorios_coordenadora ON relatorios_semana_em_foco (coordenadora_id);
CREATE INDEX idx_relatorios_status ON relatorios_semana_em_foco (status);
CREATE INDEX idx_relatorios_criado_em ON relatorios_semana_em_foco (criado_em);

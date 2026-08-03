CREATE TABLE importacao_logs (
    id BIGSERIAL PRIMARY KEY,
    tipo_entidade VARCHAR(50) NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    total_registros INT NOT NULL DEFAULT 0,
    inseridos INT NOT NULL DEFAULT 0,
    atualizados INT NOT NULL DEFAULT 0,
    ignorados INT NOT NULL DEFAULT 0,
    total_erros INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    tempo_processamento_ms DOUBLE PRECISION NOT NULL DEFAULT 0,
    erros_detalhados TEXT,
    data_importacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_importacao_logs_data ON importacao_logs(data_importacao DESC);
CREATE INDEX idx_importacao_logs_tipo ON importacao_logs(tipo_entidade);
CREATE INDEX idx_importacao_logs_status ON importacao_logs(status);

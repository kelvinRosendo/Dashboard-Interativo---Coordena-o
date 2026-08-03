CREATE TABLE coordenadoras (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    segmento VARCHAR(50) NOT NULL,
    telefone VARCHAR(20),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coordenadoras_segmento ON coordenadoras(segmento);
CREATE INDEX idx_coordenadoras_email ON coordenadoras(email);

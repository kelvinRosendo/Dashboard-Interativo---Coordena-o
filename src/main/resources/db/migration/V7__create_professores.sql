CREATE TABLE professores (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    disciplina VARCHAR(150),
    segmento VARCHAR(50) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_professores_segmento ON professores(segmento);
CREATE INDEX idx_professores_nome ON professores(nome);
CREATE INDEX idx_professores_disciplina ON professores(disciplina);

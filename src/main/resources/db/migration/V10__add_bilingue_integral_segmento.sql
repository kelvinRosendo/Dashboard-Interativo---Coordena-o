-- Atualizar constraints de segmento para incluir BILINGUE e INTEGRAL

ALTER TABLE demandas DROP CONSTRAINT IF EXISTS demandas_segmento_check;
ALTER TABLE demandas ADD CONSTRAINT demandas_segmento_check CHECK (
    segmento IN (
        'EDUCACAO_INFANTIL',
        'FUNDAMENTAL_1',
        'FUNDAMENTAL_2',
        'ENSINO_MEDIO',
        'BILINGUE',
        'INTEGRAL'
    )
);

ALTER TABLE semanas_em_foco DROP CONSTRAINT IF EXISTS semanas_em_foco_segmento_check;
ALTER TABLE semanas_em_foco ADD CONSTRAINT semanas_em_foco_segmento_check CHECK (
    segmento IN (
        'EDUCACAO_INFANTIL',
        'FUNDAMENTAL_1',
        'FUNDAMENTAL_2',
        'ENSINO_MEDIO',
        'BILINGUE',
        'INTEGRAL'
    )
);

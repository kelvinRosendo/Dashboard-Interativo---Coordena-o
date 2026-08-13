-- =====================================================
-- V14: SEED DATA PACK 2026
-- Dados oficiais de rotina de coordenacao pedagogica
-- Fonte: Data_Pack_2026 (CSVs + Painel)
-- Idempotente: ON CONFLICT DO NOTHING
-- =====================================================

-- =====================================================
-- 1. SEGMENTOS (estrutural)
-- =====================================================
INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('educacao-infantil', 'Educacao Infantil', 'Acompanhamento do Infantil: rotina, acolhimento e desenvolvimento.', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('fundamental-1', 'Fundamental 1', 'Alfabetizacao, consolidacao e intervencao no Fundamental I.', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('fundamental-2', 'Fundamental 2', 'Check-list geral, Geekie, incluso, projetos e feedback.', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('ensino-medio', 'Ensino Medio', 'Estrategias ENEM e devolutivas, por area e acao com alunos.', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('bilingue', 'Bilingue', 'Acompanhamento do segmento Bilíngue.', true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO segmentos (slug, titulo, descricao, ativo) VALUES
('integral', 'Integral', 'Acompanhamento do segmento Integral.', true)
ON CONFLICT (slug) DO NOTHING;

-- =====================================================
-- 2. COORDENADORAS (referencia para exibicao)
-- =====================================================
INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Elaine', 'elaine.bombarda@colegiosatelite.com.br', 'EDUCACAO_INFANTIL')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Elaine', 'elaine.bombarda@colegiosatelite.com.br', 'FUNDAMENTAL_1')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Edna', 'edna.boniolo@colegiosatelite.com.br', 'FUNDAMENTAL_2')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Amanda', 'amanda.souza@colegiosatelite.com.br', 'FUNDAMENTAL_2')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Ananda', 'ananda.caballero@colegiosatelite.com.br', 'FUNDAMENTAL_2')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Edna', 'edna.boniolo@colegiosatelite.com.br', 'ENSINO_MEDIO')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Amanda', 'amanda.souza@colegiosatelite.com.br', 'ENSINO_MEDIO')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Ananda', 'ananda.caballero@colegiosatelite.com.br', 'ENSINO_MEDIO')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Lilian', 'lilian@colegiosatelite.com.br', 'FUNDAMENTAL_2')
ON CONFLICT DO NOTHING;

INSERT INTO coordenadoras (nome, email, segmento) VALUES
('Lilian', 'lilian@colegiosatelite.com.br', 'ENSINO_MEDIO')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. SEMANAS EM FOCO (Data Pack 2026 - Ago/Nov)
-- Fonte: Painel.csv
-- =====================================================

-- AGOSTO 2026
INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('EDUCACAO_INFANTIL', 'Educacao Infantil em Foco', 'Acompanhamento do Infantil: rotina, acolhimento e desenvolvimento.', 'ALTA', '2026-08-03', '2026-08-07', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_1', 'Fund. Anos Iniciais em Foco', 'Alfabetizacao, consolidacao e intervencao no Fundamental I.', 'ALTA', '2026-08-10', '2026-08-14', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('ENSINO_MEDIO', 'Ensino Medio em Foco', 'Check-list geral, Geekie e dados, por area e acao com alunos.', 'ALTA', '2026-08-17', '2026-08-21', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_2', 'Fund. Anos Finais em Foco', 'Check-list geral, Geekie, incluso, projetos e feedback.', 'ALTA', '2026-08-24', '2026-08-28', false, NOW())
ON CONFLICT DO NOTHING;

-- SETEMBRO 2026
INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_2', 'Fund. Anos Finais em Foco', 'Continuidade do acompanhamento do Fundamental II.', 'ALTA', '2026-08-31', '2026-09-04', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('EDUCACAO_INFANTIL', 'Educacao Infantil em Foco', 'Rotina e desenvolvimento do Infantil.', 'ALTA', '2026-09-08', '2026-09-11', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_1', 'Fund. Anos Iniciais em Foco', 'Acompanhamento pedagogico do Fundamental I.', 'ALTA', '2026-09-14', '2026-09-18', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('ENSINO_MEDIO', 'Ensino Medio em Foco', 'Estrategias ENEM e devolutivas.', 'ALTA', '2026-09-21', '2026-09-25', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_2', 'Fund. Anos Finais em Foco', 'Fechamento do trimestre Fundamental II.', 'ALTA', '2026-09-28', '2026-10-02', false, NOW())
ON CONFLICT DO NOTHING;

-- OUTUBRO 2026
INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_1', 'Fund. Anos Iniciais em Foco', 'Retomada do Fundamental I apos ferias.', 'ALTA', '2026-10-05', '2026-10-09', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('EDUCACAO_INFANTIL', 'Educacao Infantil em Foco', 'Novo ciclo no Infantil.', 'ALTA', '2026-10-13', '2026-10-16', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('ENSINO_MEDIO', 'Ensino Medio em Foco', 'Planejamento final do ano Ensino Medio.', 'ALTA', '2026-10-19', '2026-10-23', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_2', 'Fund. Anos Finais em Foco', 'Acompanhamento Fundamental II.', 'ALTA', '2026-10-26', '2026-10-30', false, NOW())
ON CONFLICT DO NOTHING;

-- NOVEMBRO 2026
INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('EDUCACAO_INFANTIL', 'Educacao Infantil em Foco', 'Rotina final de ano no Infantil.', 'ALTA', '2026-11-03', '2026-11-06', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('EDUCACAO_INFANTIL', 'Educacao Infantil em Foco', 'Educacao Infantil - Segundo ciclo.', 'ALTA', '2026-11-09', '2026-11-13', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_1', 'Fund. Anos Iniciais em Foco', 'Avaliacoes finais Fundamental I.', 'ALTA', '2026-11-16', '2026-11-20', false, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO semanas_em_foco (segmento, titulo, descricao, prioridade, data_inicio, data_fim, ativa, atualizado_em) VALUES
('FUNDAMENTAL_2', 'Fund. Anos Finais em Foco', 'Encerramento Fundamental II.', 'ALTA', '2026-11-23', '2026-11-27', false, NOW())
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. CARDS - ROTINA COORDENADORES (Data Pack 2026)
-- Representam a 1a semana de cada segmento
-- =====================================================

-- EDUCACAO INFANTIL (Semana 10/08)
INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EI - Inicio de Rotina', 'Observar as duas salas (15-20 min cada). Avaliar acolhimento das criancas (entrada -> atividade). Analisar planejamento do dia e intencionalidade. Verificar interacao professor x aluno.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-10', 'Elaine', 'Educacao Infantil - Segunda-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EI - Interacao e Desenvolvimento', 'Observar as duas salas. Avaliar linguagem e comunicacao das criancas. Avaliar mediacao docente e engajamento discente. Registrar pontos de atencao e orientar pratica docente.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-11', 'Elaine', 'Educacao Infantil - Terca-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EI - Aprendizagem', 'Observar as duas salas. Avaliar intencionalidade pedagogica e engajamento dos alunos. Verificar desenvolvimento (coordenacao, fala, socializacao). Observar desenvolvimento, registros e fornecer devolutiva estruturada.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-12', 'Elaine', 'Educacao Infantil - Quarta-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EI - Autonomia e Rotina', 'Observar as duas salas. Avaliar autonomia das criancas e organizacao da rotina. Analisar comportamento coletivo e preparo para o Fundamental. Orientar ajustes pedagogicos.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-13', 'Elaine', 'Educacao Infantil - Quinta-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EI - Fechamento e Visao Geral', 'Monitorar turmas prioritarias e revisar alunos com dificuldades. Verificar aplicacao de ajustes e registrar pontos da semana. Listar alunos para acompanhamento continuo e intervencao. Acompanhar PEIs. Entrega para Direcao as 15h.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-14', 'Elaine', 'Educacao Infantil - Sexta-feira', NOW())
ON CONFLICT DO NOTHING;

-- FUNDAMENTAL 1 (Semana 17/08)
INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FI - Alfabetizacao', 'Observar aula de alfabetizacao. Verificar nivel de leitura. Identificar alunos com dificuldade. Registrar para recomposicao.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-17', 'Elaine', 'Fund. Anos Iniciais - Segunda-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FI - Consolidacao', 'Observar rotina de leitura/escrita. Verificar fluencia leitora. Analisar producao escrita. Apoiar professora.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-18', 'Elaine', 'Fund. Anos Iniciais - Terca-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FI - Intervencao', 'Analisar resultados de avaliacoes (caso houver). Identificar habilidades nao consolidadas. Planejar intervencao. Acompanhar PEIs.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-19', 'Elaine', 'Fund. Anos Iniciais - Quarta-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FI - Aprendizagem', 'Observar metodologia. Verificar compreensao leitora. Identificar dificuldades coletivas. Acompanhar PEIs.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-20', 'Elaine', 'Fund. Anos Iniciais - Quinta-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FI - Resultado', 'Analisar desempenho geral. Identificar alunos criticos. Organizar lista de recomposicao. Entrega para Direcao as 15h.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-21', 'Elaine', 'Fund. Anos Iniciais - Sexta-feira', NOW())
ON CONFLICT DO NOTHING;

-- FUNDAMENTAL 2 (Semana 24/08)
INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FII - Check-list Geral', 'Observar engajamento e adaptacao dos alunos. Verificar organizacao dos estudos (caderno e rotina). Identificar dificuldades iniciais.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-24', 'Edna/Amanda/Ananda', 'Fund. Anos Finais - Segunda-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FII - Geekie e Dados', 'Consultar relatorios de participacao. Verificar alunos com baixa adesao. Analisar desempenho inicial.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-24', 'Edna/Amanda/Ananda', 'Fund. Anos Finais - Geekie', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FII - Inclusao', 'Verificar alunos com necessidade de adaptacao. Orientar professores sobre material adaptado.', 'ROTINA_COORDENADORES', 'MEDIA', 'PENDENTE', '2026-08-24', 'Edna/Amanda/Ananda', 'Fund. Anos Finais - Inclusao', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FII - Feedback e Recomposicao', 'Orientar professores sobre devolutivas de atividades. Iniciar cultura de correcao comentada.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-24', 'Edna/Amanda/Ananda', 'Fund. Anos Finais - Feedback', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('FII - Projetos', 'Verificar andamento dos projetos. Avaliar engajamento e resultados parciais.', 'ROTINA_COORDENADORES', 'MEDIA', 'PENDENTE', '2026-08-24', 'Edna/Amanda/Ananda', 'Fund. Anos Finais - Projetos', NOW())
ON CONFLICT DO NOTHING;

-- ENSINO MEDIO (Semana 17/08)
INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EM - Check-list Geral', 'Verificar rotina de estudos dos alunos. Cobrar uso da Geekie (acesso e tempo). Identificar alunos sem engajamento. Acompanhar PEIs.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-17', 'Edna/Amanda/Ananda', 'Ensino Medio - Segunda-feira', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EM - Geekie e Dados', 'Analisar relatorios de participacao. Identificar alunos nivel 1. Listar habilidades com desempenho abaixo de 60%.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-17', 'Edna/Amanda/Ananda', 'Ensino Medio - Geekie', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EM - Por Area', 'Linguagens: Leitura e interpretacao (base ENEM). Exatas: Matematica Basica (base ENEM). Humanas: Interpretacao e analise critica (base ENEM).', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-17', 'Edna/Amanda/Ananda', 'Ensino Medio - Por Area', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EM - Acao com Alunos', 'Conversar com alunos nivel 1. Definir meta individual (subir para nivel 2).', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-17', 'Edna/Amanda/Ananda', 'Ensino Medio - Acao', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, data_evento, responsavel, observacoes, data_criacao) VALUES
('EM - Gestao e Fechamento', 'Atualizar ranking interno por turma. Listar alunos por nivel (1 a 4). Verificar evolucao semanal. Dar devolutiva para professores. Cobrar plano de acao claro.', 'ROTINA_COORDENADORES', 'ALTA', 'PENDENTE', '2026-08-21', 'Edna/Amanda/Ananda', 'Ensino Medio - Sexta-feira', NOW())
ON CONFLICT DO NOTHING;

-- =====================================================
-- 5. CARDS - CHECKLIST TRIMESTRAL
-- =====================================================
INSERT INTO cards (titulo, descricao, categoria, prioridade, status, responsavel, observacoes, data_criacao) VALUES
('Checklist Trimestral - Geekie e Dados', 'Analise participacao diaria. Analise desempenho por habilidade. Uso de dados para orientar decisoes.', 'ROTINA_ADMINISTRATIVA', 'ALTA', 'PENDENTE', 'Coordenacao', 'Checklist trimestral - Geekie', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, responsavel, observacoes, data_criacao) VALUES
('Checklist Trimestral - Professores', 'Verificar uso de dados no planejamento. Verificar devolutiva das provas. Verificar cobranca de estudo dos alunos.', 'ROTINA_ADMINISTRATIVA', 'ALTA', 'PENDENTE', 'Coordenacao', 'Checklist trimestral - Professores', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, responsavel, observacoes, data_criacao) VALUES
('Checklist Trimestral - Inclusao', 'Constatou material adaptado quando necessario. Verificou acompanhamento adequado.', 'ROTINA_ADMINISTRATIVA', 'MEDIA', 'PENDENTE', 'Coordenacao', 'Checklist trimestral - Inclusao', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cards (titulo, descricao, categoria, prioridade, status, responsavel, observacoes, data_criacao) VALUES
('Checklist Trimestral - Alunos', 'Checou plano de estudo ativo. Verificou clareza das cobrancas. Observou evolucao.', 'ROTINA_ADMINISTRATIVA', 'MEDIA', 'PENDENTE', 'Coordenacao', 'Checklist trimestral - Alunos', NOW())
ON CONFLICT DO NOTHING;

-- =====================================================
-- 6. COMUNICADOS (dados iniciais)
-- =====================================================
INSERT INTO comunicados (titulo, conteudo, data_criacao, data_atualizacao) VALUES
('Regras de conduta para o periodo de provas', 'Durante o periodo de avaliacao, todos os alunos devem seguir o regimento interno. Celulares devem ser entregues na entrada da sala. Qualquer irregularidade sera registrada e comunicada aos responsaveis.', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO comunicados (titulo, conteudo, data_criacao, data_atualizacao) VALUES
('Reuniao de pais e mestres', 'A reuniao trimestral de pais e mestres sera realizada na quinta-feira as 19h no auditorio. A presenca e obrigatoria para todos os responsaveis de alunos do Ensino Fundamental I.', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO comunicados (titulo, conteudo, data_criacao, data_atualizacao) VALUES
('Campanha de arrecadacao solidaria', 'A escola promove uma campanha de arrecadacao de alimentos nao pereciveis. As doacoes podem ser entregues na recepcao da escola.', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO comunicados (titulo, conteudo, data_criacao, data_atualizacao) VALUES
('Rotina de Coordenacao Pedagogica', 'A rotina de coordenacao pedagogica foi atualizada. As coordenadoras devem seguir o cronograma semanal de visitas as turmas, conforme o segmento atribuido.', NOW(), NOW())
ON CONFLICT DO NOTHING;

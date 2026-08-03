package br.com.escola.dashboard.config;

import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.entity.Coordenadora;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.entity.comunicado;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.repository.CardRepository;
import br.com.escola.dashboard.repository.CoordenadoraRepository;
import br.com.escola.dashboard.repository.ComunicadoRepository;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final CardRepository cardRepository;
    private final ComunicadoRepository comunicadoRepository;
    private final CoordenadoraRepository coordenadoraRepository;
    private final SemanaEmFocoRepository semanaEmFocoRepository;

    public DataInitializer(CardRepository cardRepository,
                           ComunicadoRepository comunicadoRepository,
                           CoordenadoraRepository coordenadoraRepository,
                           SemanaEmFocoRepository semanaEmFocoRepository) {
        this.cardRepository = cardRepository;
        this.comunicadoRepository = comunicadoRepository;
        this.coordenadoraRepository = coordenadoraRepository;
        this.semanaEmFocoRepository = semanaEmFocoRepository;
    }

    @Override
    public void run(String... args) {
        popularCoordenadoras();
        popularSemanasEmFoco();
        popularCards();
        popularComunicados();
    }

    private void popularCoordenadoras() {
        if (coordenadoraRepository.count() > 0) return;

        List<Coordenadora> coordenadoras = List.of(
                criarCoordenadora("Elaine", null, SegmentoCoordenacao.EDUCACAO_INFANTIL, null),
                criarCoordenadora("Elaine", null, SegmentoCoordenacao.FUNDAMENTAL_1, null),
                criarCoordenadora("Edna", null, SegmentoCoordenacao.FUNDAMENTAL_2, null),
                criarCoordenadora("Amanda", null, SegmentoCoordenacao.FUNDAMENTAL_2, null),
                criarCoordenadora("Ananda", null, SegmentoCoordenacao.FUNDAMENTAL_2, null),
                criarCoordenadora("Lilian", null, SegmentoCoordenacao.FUNDAMENTAL_2, null),
                criarCoordenadora("Edna", null, SegmentoCoordenacao.ENSINO_MEDIO, null),
                criarCoordenadora("Amanda", null, SegmentoCoordenacao.ENSINO_MEDIO, null),
                criarCoordenadora("Ananda", null, SegmentoCoordenacao.ENSINO_MEDIO, null),
                criarCoordenadora("Lilian", null, SegmentoCoordenacao.ENSINO_MEDIO, null)
        );
        coordenadoraRepository.saveAll(coordenadoras);
    }

    private void popularSemanasEmFoco() {
        if (semanaEmFocoRepository.count() > 0) return;

        List<SemanaEmFoco> semanas = List.of(
                // AGOSTO 2026
                criarSemana(SegmentoCoordenacao.EDUCACAO_INFANTIL, "Educacao Infantil em Foco", "Acompanhamento do Infantil: rotina, acolhimento e desenvolvimento.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 7)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_1, "Fund. Anos Iniciais em Foco", "Alfabetizacao, consolidacao e intervencao no Fundamental I.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 14)),
                criarSemana(SegmentoCoordenacao.ENSINO_MEDIO, "Ensino Medio em Foco", "Check-list geral, Geekie e dados, por area e acao com alunos.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 8, 17), LocalDate.of(2026, 8, 21)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_2, "Fund. Anos Finais em Foco", "Check-list geral, Geekie, incluso, projetos e feedback.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 8, 24), LocalDate.of(2026, 8, 28)),

                // SETEMBRO 2026
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_2, "Fund. Anos Finais em Foco", "Continuidade do acompanhamento do Fundamental II.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 8, 31), LocalDate.of(2026, 9, 4)),
                criarSemana(SegmentoCoordenacao.EDUCACAO_INFANTIL, "Educacao Infantil em Foco", "Rotina e desenvolvimento do Infantil.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 11)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_1, "Fund. Anos Iniciais em Foco", "Acompanhamento pedagogico do Fundamental I.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 18)),
                criarSemana(SegmentoCoordenacao.ENSINO_MEDIO, "Ensino Medio em Foco", "Estrategias ENEM e devolutivas.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 25)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_2, "Fund. Anos Finais em Foco", "Fechamento do trimestre Fundamental II.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 9, 28), LocalDate.of(2026, 10, 2)),

                // OUTUBRO 2026
                criarSemana(SegmentoCoordenacao.EDUCACAO_INFANTIL, "Educacao Infantil em Foco", "Novo ciclo no Infantil.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 9)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_1, "Fund. Anos Iniciais em Foco", "Retomada do Fundamental I apos ferias.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 10, 13), LocalDate.of(2026, 10, 16)),
                criarSemana(SegmentoCoordenacao.ENSINO_MEDIO, "Ensino Medio em Foco", "Planejamento final do ano Ensino Medio.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 10, 19), LocalDate.of(2026, 10, 23)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_2, "Fund. Anos Finais em Foco", "Acompanhamento Fundamental II.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 10, 26), LocalDate.of(2026, 10, 30)),

                // NOVEMBRO 2026
                criarSemana(SegmentoCoordenacao.EDUCACAO_INFANTIL, "Educacao Infantil em Foco", "Rotina final de ano no Infantil.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 6)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_1, "Fund. Anos Iniciais em Foco", "Avaliacoes finais Fundamental I.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 13)),
                criarSemana(SegmentoCoordenacao.ENSINO_MEDIO, "Ensino Medio em Foco", "Fechamento e encerramento Ensino Medio.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 20)),
                criarSemana(SegmentoCoordenacao.FUNDAMENTAL_2, "Fund. Anos Finais em Foco", "Encerramento Fundamental II.", PrioridadeDemanda.ALTA, LocalDate.of(2026, 11, 23), LocalDate.of(2026, 11, 27))
        );
        semanaEmFocoRepository.saveAll(semanas);
    }

    private void popularCards() {
        LocalDateTime agora = LocalDateTime.now();

        // EDUCAÇÃO INFANTIL - Rotina semanal
        salvarCategoriaSeNecessario(CategoriaCard.ROTINA_COORDENADORES, List.of(
                criarCard("EI - Inicio de Rotina", "Observar as duas salas (15-20 min cada). Avaliar acolhimento das criancas (entrada -> atividade). Analisar planejamento do dia e intencionalidade. Verificar interacao professor x aluno.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 10), "Elaine", "Educacao Infantil - Segunda-feira", agora),
                criarCard("EI - Interacao e Desenvolvimento", "Observar as duas salas. Avaliar linguagem e comunicacao das criancas. Avaliar mediacao docente e engajamento discente. Registrar pontos de atencao e orientar pratica docente.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 11), "Elaine", "Educacao Infantil - Terca-feira", agora),
                criarCard("EI - Aprendizagem", "Observar as duas salas. Avaliar intencionalidade pedagogica e engajamento dos alunos. Verificar desenvolvimento (coordenacao, fala, socializacao). Observar desenvolvimento, registros e fornecer devolutiva estruturada.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 12), "Elaine", "Educacao Infantil - Quarta-feira", agora),
                criarCard("EI - Autonomia e Rotina", "Observar as duas salas. Avaliar autonomia das criancas e organizacao da rotina. Analisar comportamento coletivo e preparo para o Fundamental. Orientar ajustes pedagogicos.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 13), "Elaine", "Educacao Infantil - Quinta-feira", agora),
                criarCard("EI - Fechamento e Visao Geral", "Monitorar turmas prioritarias e revisar alunos com dificuldades. Verificar aplicacao de ajustes e registrar pontos da semana. Listar alunos para acompanhamento continuo e intervencao. Acompanhar PEIs. Entrega para Direcao as 15h.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 14), "Elaine", "Educacao Infantil - Sexta-feira", agora)
        ));

        // FUNDAMENTAL I - Rotina semanal
        salvarCategoriaSeNecessario(CategoriaCard.ROTINA_COORDENADORES, List.of(
                criarCard("FI - Alfabetizacao", "Observar aula de alfabetizacao. Verificar nivel de leitura. Identificar alunos com dificuldade. Registrar para recomposicao.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 17), "Elaine", "Fund. Anos Iniciais - Segunda-feira", agora),
                criarCard("FI - Consolidacao", "Observar rotina de leitura/escrita. Verificar fluencia leitora. Analisar producao escrita. Apoiar professora.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 18), "Elaine", "Fund. Anos Iniciais - Terca-feira", agora),
                criarCard("FI - Intervencao", "Analisar resultados de avaliacoes (caso houver). Identificar habilidades nao consolidadas. Planejar intervencao. Acompanhar PEIs.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 19), "Elaine", "Fund. Anos Iniciais - Quarta-feira", agora),
                criarCard("FI - Aprendizagem", "Observar metodologia. Verificar compreensao leitora. Identificar dificuldades coletivas. Acompanhar PEIs.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 20), "Elaine", "Fund. Anos Iniciais - Quinta-feira", agora),
                criarCard("FI - Resultado", "Analisar desempenho geral. Identificar alunos criticos. Organizar lista de recomposicao. Entrega para Direcao as 15h.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 21), "Elaine", "Fund. Anos Iniciais - Sexta-feira", agora)
        ));

        // FUNDAMENTAL II - Rotina semanal
        salvarCategoriaSeNecessario(CategoriaCard.ROTINA_COORDENADORES, List.of(
                criarCard("FII - Check-list Geral", "Observar engajamento e adaptacao dos alunos. Verificar organizacao dos estudos (caderno e rotina). Identificar dificuldades iniciais.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 24), "Edna/Amanda/Ananda/Lilian", "Fund. Anos Finais - Segunda-feira", agora),
                criarCard("FII - Geekie e Dados", "Consultar relatorios de participacao. Verificar alunos com baixa adesao. Analisar desempenho inicial.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 24), "Edna/Amanda/Ananda/Lilian", "Fund. Anos Finais - Geekie", agora),
                criarCard("FII - Inclusao", "Verificar alunos com necessidade de adaptacao. Orientar professores sobre material adaptado.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.MEDIA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 24), "Edna/Amanda/Ananda/Lilian", "Fund. Anos Finais - Inclusao", agora),
                criarCard("FII - Feedback e Recomposicao", "Orientar professores sobre devolutivas de atividades. Iniciar cultura de correcao comentada.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 24), "Edna/Amanda/Ananda/Lilian", "Fund. Anos Finais - Feedback", agora),
                criarCard("FII - Projetos", "Verificar andamento dos projetos. Avaliar engajamento e resultados parciais.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.MEDIA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 24), "Edna/Amanda/Ananda/Lilian", "Fund. Anos Finais - Projetos", agora)
        ));

        // ENSINO MÉDIO - Rotina semanal
        salvarCategoriaSeNecessario(CategoriaCard.ROTINA_COORDENADORES, List.of(
                criarCard("EM - Check-list Geral", "Verificar rotina de estudos dos alunos. Cobrar uso da Geekie (acesso e tempo). Identificar alunos sem engajamento. Acompanhar PEIs.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 17), "Edna/Amanda/Ananda/Lilian", "Ensino Medio - Segunda-feira", agora),
                criarCard("EM - Geekie e Dados", "Analisar relatorios de participacao. Identificar alunos nivel 1. Listar habilidades com desempenho abaixo de 60%.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 17), "Edna/Amanda/Ananda/Lilian", "Ensino Medio - Geekie", agora),
                criarCard("EM - Por Area", "Linguagens: Leitura e interpretacao (base ENEM). Exatas: Matematica Basica (base ENEM). Humanas: Interpretacao e analise critica (base ENEM).", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 17), "Edna/Amanda/Ananda/Lilian", "Ensino Medio - Por Area", agora),
                criarCard("EM - Acao com Alunos", "Conversar com alunos nivel 1. Definir meta individual (subir para nivel 2).", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 17), "Edna/Amanda/Ananda/Lilian", "Ensino Medio - Acao", agora),
                criarCard("EM - Gestao e Fechamento", "Atualizar ranking interno por turma. Listar alunos por nivel (1 a 4). Verificar evolucao semanal. Dar devolutiva para professores. Cobrar plano de acao claro.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 8, 21), "Edna/Amanda/Ananda/Lilian", "Ensino Medio - Sexta-feira", agora)
        ));

        // CHECKLIST TRIMESTRAL
        salvarCategoriaSeNecessario(CategoriaCard.ROTINA_ADMINISTRATIVA, List.of(
                criarCard("Checklist Trimestral - Geekie e Dados", "Analise participacao diaria. Analise desempenho por habilidade. Uso de dados para orientar decisoes.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.ALTA, StatusCard.PENDENTE, null, "Coordenacao", "Checklist trimestral - Geekie", agora),
                criarCard("Checklist Trimestral - Professores", "Verificar uso de dados no planejamento. Verificar devolutiva das provas. Verificar cobranca de estudo dos alunos.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.ALTA, StatusCard.PENDENTE, null, "Coordenacao", "Checklist trimestral - Professores", agora),
                criarCard("Checklist Trimestral - Inclusao", "Constatou material adaptado quando necessario. Verificou acompanhamento adequado.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, "Coordenacao", "Checklist trimestral - Inclusao", agora),
                criarCard("Checklist Trimestral - Alunos", "Checou plano de estudo ativo. Verificou clareza das cobrancas. Observou evolucao.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, "Coordenacao", "Checklist trimestral - Alunos", agora)
        ));
    }

    private void popularComunicados() {
        if (comunicadoRepository.count() > 0) return;

        List<comunicado> comunicados = List.of(
                criarComunicado("Regras de conduta para o periodo de provas",
                        "Durante o periodo de avaliacao, todos os alunos devem seguir o regimento interno. "
                        + "Celulares devem ser entregues na entrada da sala. Qualquer irregularidade sera registrada "
                        + "e comunicada aos responsaveis."),
                criarComunicado("Reuniao de pais e mestres",
                        "A reuniao trimestral de pais e mestres sera realizada na quinta-feira as 19h no auditorio. "
                        + "A presenca e obrigatoria para todos os responsaveis de alunos do Ensino Fundamental I."),
                criarComunicado("Campanha de arrecadacao solidaria",
                        "A escola promove uma campanha de arrecadacao de alimentos nao pereciveis. "
                        + "As doacoes podem ser entregues na recepcao da escola ate o dia 30/05."),
                criarComunicado("Rotina de Coordenacao Pedagogica 2026",
                        "A rotina de coordenacao pedagogica foi atualizada para 2026. As coordenadoras devem "
                        + "seguir o cronograma semanal de visitas as turmas, conforme o segmento atribuido.")
        );
        comunicadoRepository.saveAll(comunicados);
    }

    private Coordenadora criarCoordenadora(String nome, String email, SegmentoCoordenacao segmento, String telefone) {
        Coordenadora c = new Coordenadora();
        c.setNome(nome);
        if (email != null) c.setEmail(email);
        c.setSegmento(segmento);
        if (telefone != null) c.setTelefone(telefone);
        return c;
    }

    private SemanaEmFoco criarSemana(SegmentoCoordenacao segmento, String titulo, String descricao,
                                     PrioridadeDemanda prioridade, LocalDate dataInicio, LocalDate dataFim) {
        SemanaEmFoco s = new SemanaEmFoco();
        s.setSegmento(segmento);
        s.setTitulo(titulo);
        s.setDescricao(descricao);
        s.setPrioridade(prioridade);
        s.setDataInicio(dataInicio);
        s.setDataFim(dataFim);
        s.setAtiva(false);
        return s;
    }

    private comunicado criarComunicado(String titulo, String conteudo) {
        comunicado c = new comunicado();
        c.setTitulo(titulo);
        c.setConteudo(conteudo);
        return c;
    }

    private void salvarCategoriaSeNecessario(CategoriaCard categoria, List<Card> cards) {
        if (cardRepository.existsByCategoria(categoria)) {
            return;
        }
        cardRepository.saveAll(Objects.requireNonNull(cards));
    }

    private Card criarCard(String titulo, String descricao, CategoriaCard categoria,
                           PrioridadeCard prioridade, StatusCard status, LocalDate dataEvento,
                           String responsavel, String observacoes, LocalDateTime dataCriacao) {
        Card card = new Card();
        card.setTitulo(titulo);
        card.setDescricao(descricao);
        card.setCategoria(categoria);
        card.setPrioridade(prioridade);
        card.setStatus(status);
        card.setDataEvento(dataEvento);
        card.setResponsavel(responsavel);
        card.setObservacoes(observacoes);
        card.setDataCriacao(dataCriacao);
        return card;
    }
}

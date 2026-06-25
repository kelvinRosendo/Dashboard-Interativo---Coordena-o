package br.com.escola.dashboard.config;

import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.entity.Comunicado;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.repository.CardRepository;
import br.com.escola.dashboard.repository.ComunicadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
///import org.springframework.lang.NonNull;
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

    public DataInitializer(CardRepository cardRepository, ComunicadoRepository comunicadoRepository) {
        this.cardRepository = cardRepository;
        this.comunicadoRepository = comunicadoRepository;
    }

    @Override
    public void run(String... args) {
        LocalDateTime agora = LocalDateTime.now();

        salvarCategoriaSeNecessario(
                CategoriaCard.SUBSTITUICAO,
                List.of(
                        criarCard("Substituicao 1oA", "Cobertura da aula de Matematica no primeiro horario.", CategoriaCard.SUBSTITUICAO, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 17), "Prof. Ana Paula", "Confirmar material com a coordenacao.", agora.minusHours(6)),
                        criarCard("Reforco 7oB", "Aula de reforco de Lingua Portuguesa as 10:20.", CategoriaCard.SUBSTITUICAO, PrioridadeCard.MEDIA, StatusCard.EM_ANDAMENTO, LocalDate.of(2026, 4, 17), "Prof. Carlos Henrique", "Sala 12.", agora.minusHours(5)),
                        criarCard("Atendimento pedagogico", "Atendimento individual de alunos no periodo da tarde.", CategoriaCard.SUBSTITUICAO, PrioridadeCard.BAIXA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 17), "Prof. Juliana", "Organizar ordem de atendimento.", agora.minusHours(4))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.FALTA_PROFESSOR,
                List.of(
                        criarCard("Ausencia no turno da manha", "Ausencia justificada por atestado medico.", CategoriaCard.FALTA_PROFESSOR, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 17), "Prof. Marcos", "Necessario remanejamento de aulas.", agora.minusHours(3)),
                        criarCard("Saida antecipada", "Saida as 15h para consulta agendada.", CategoriaCard.FALTA_PROFESSOR, PrioridadeCard.MEDIA, StatusCard.EM_ANDAMENTO, LocalDate.of(2026, 4, 17), "Prof. Renata", "Ajustar cobertura das ultimas aulas.", agora.minusHours(2)),
                        criarCard("Falta no periodo da tarde", "Ausencia confirmada para o turno vespertino.", CategoriaCard.FALTA_PROFESSOR, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 18), "Prof. Luciana", "Avisar secretaria e coordenacao.", agora.minusHours(1))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.EVENTO, 
                List.of(
                        criarCard("Conselho de classe", "Reuniao de fechamento do primeiro trimestre com professores.", CategoriaCard.EVENTO, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 20), "Coordenacao Pedagogica", "Levar relatorios de desempenho.", agora),
                        criarCard("Simulado geral", "Aplicacao de simulado para turmas do ensino fundamental.", CategoriaCard.EVENTO, PrioridadeCard.MEDIA, StatusCard.EM_ANDAMENTO, LocalDate.of(2026, 4, 22), "Equipe Pedagogica", "Organizar salas e provas.", agora.plusHours(1)),
                        criarCard("Feira cultural", "Evento com apresentacoes, exposicoes e trabalhos dos alunos.", CategoriaCard.EVENTO, PrioridadeCard.MEDIA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 26), "Coordenacao Geral", "Confirmar estrutura e cronograma.", agora.plusHours(2)),
                        criarCard("Entrega de boletins", "Entrega presencial de boletins para responsaveis.", CategoriaCard.EVENTO, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 24), "Secretaria Escolar", "Separar turmas por horario.", agora.plusHours(3))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.ROTINA_ADMINISTRATIVA,
                List.of(
                        criarCard("Conferir frequencia diaria", "Validar faltas e inconsistencias no sistema.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.ALTA, StatusCard.EM_ANDAMENTO, null, "Secretaria", "Finalizar ate 11h.", agora.plusHours(4)),
                        criarCard("Atualizar mural interno", "Substituir comunicados antigos e inserir novos avisos.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.BAIXA, StatusCard.PENDENTE, null, "Coordenacao", "Revisar calendario escolar.", agora.plusHours(5)),
                        criarCard("Validar pedidos de material", "Conferir solicitacoes enviadas pelos professores.", CategoriaCard.ROTINA_ADMINISTRATIVA, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, "Setor Administrativo", "Priorizar materiais para avaliacoes.", agora.plusHours(6))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.ROTINA_COORDENADORES,
                List.of(
                        criarCard("Checklist da coordenacao", "Revisar pendencias da rotina pedagogica do dia.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.ALTA, StatusCard.EM_ANDAMENTO, null, "Coordenacao", "Atualizar conclusoes no painel.", agora.plusHours(7)),
                        criarCard("Acompanhamento dos segmentos", "Conferir demandas abertas por etapa da escola.", CategoriaCard.ROTINA_COORDENADORES, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, "Coordenacao", "Priorizar semana em foco.", agora.plusHours(8))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.SEMANA_EM_FOCO,
                List.of(
                        criarCard("Fundamental 1 em foco", "Acompanhar adaptacao das turmas, demandas pedagogicas e comunicados da semana.", CategoriaCard.SEMANA_EM_FOCO, PrioridadeCard.ALTA, StatusCard.EM_ANDAMENTO, LocalDate.now(), "Fundamental 1", "Validar prioridades com as coordenadoras.", agora.plusHours(9)),
                        criarCard("Manutencao do foco semanal", "Conferir se as demandas do segmento estao atualizadas para exibicao na TV.", CategoriaCard.SEMANA_EM_FOCO, PrioridadeCard.MEDIA, StatusCard.PENDENTE, LocalDate.now().plusDays(1), "Coordenacao", "Atualizar cards de manutencao.", agora.plusHours(10))
                )
        );

        salvarComunicadosSeNecessario();
    }

    private void salvarComunicadosSeNecessario() {
        if (comunicadoRepository.count() > 0) {
            return;
        }

        comunicadoRepository.saveAll(List.of(
                criarComunicado("Regras de conduta para o periodo de provas",
                        "Durante o periodo de avaliacao, todos os alunos devem seguir o regimento interno. "
                        + "Celulares devem ser entregues na entrada da sala. Qualquer irregularidade sera registrada "
                        + "e comunicada aos responsaveis."),
                criarComunicado("Reuniao de pais e mestres",
                        "A reuniao trimestral de pais e mestres sera realizada na quinta-feira as 19h no auditorio. "
                        + "A presenca e obrigatoria para todos os responsaveis de alunos do Ensino Fundamental I."),
                criarComunicado("Campanha de arrecadacao solidaria",
                        "A escola promove uma campanha de arrecadacao de alimentos nao pereciveis. "
                        + "As doacoes podem ser entregues na recepcao da escola ate o dia 30/05.")
        ));
    }

    private Comunicado criarComunicado(String titulo, String conteudo) {
        Comunicado comunicado = new Comunicado();
        comunicado.setTitulo(titulo);
        comunicado.setConteudo(conteudo);
        return comunicado;
    }

    private void salvarCategoriaSeNecessario(CategoriaCard categoria, List<Card> cards) {
        if (cardRepository.existsByCategoria(categoria)) {
            return;
        }

        cardRepository.saveAll(Objects.requireNonNull(cards));
    }

    private Card criarCard(String titulo,
                           String descricao,
                           CategoriaCard categoria,
                           PrioridadeCard prioridade,
                           StatusCard status,
                           LocalDate dataEvento,
                           String responsavel,
                           String observacoes,
                           LocalDateTime dataCriacao) {
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

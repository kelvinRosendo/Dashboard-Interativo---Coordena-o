package br.com.escola.dashboard.config;

import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CardRepository cardRepository;

    public DataInitializer(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public void run(String... args) {
        LocalDateTime agora = LocalDateTime.now();
        salvarCategoriaSeNecessario(
                CategoriaCard.AVISO_NOTA,
                List.of(
                        criarCard("Uso obrigatorio de cracha", "Toda a equipe deve utilizar cracha visivel durante o expediente.", CategoriaCard.AVISO_NOTA, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, null, "Reforcar orientacao na entrada.", agora.minusHours(9)),
                        criarCard("Prazo para lancamento de notas", "O lancamento de notas do trimestre deve ser concluido ate 25/04.", CategoriaCard.AVISO_NOTA, PrioridadeCard.ALTA, StatusCard.EM_ANDAMENTO, null, null, "Conferir pendencias por turma.", agora.minusHours(8)),
                        criarCard("Reuniao com responsaveis", "Reuniao geral com responsaveis na sexta-feira as 18h no auditorio.", CategoriaCard.AVISO_NOTA, PrioridadeCard.ALTA, StatusCard.PENDENTE, null, null, "Preparar lista de presenca.", agora.minusHours(7))
                )
        );

        salvarCategoriaSeNecessario(
                CategoriaCard.HORARIO_PROFESSOR,
                List.of(
                        criarCard("Substituicao 1oA", "Cobertura da aula de Matematica no primeiro horario.", CategoriaCard.HORARIO_PROFESSOR, PrioridadeCard.ALTA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 17), "Prof. Ana Paula", "Confirmar material com a coordenacao.", agora.minusHours(6)),
                        criarCard("Reforco 7oB", "Aula de reforco de Lingua Portuguesa as 10:20.", CategoriaCard.HORARIO_PROFESSOR, PrioridadeCard.MEDIA, StatusCard.EM_ANDAMENTO, LocalDate.of(2026, 4, 17), "Prof. Carlos Henrique", "Sala 12.", agora.minusHours(5)),
                        criarCard("Atendimento pedagogico", "Atendimento individual de alunos no periodo da tarde.", CategoriaCard.HORARIO_PROFESSOR, PrioridadeCard.BAIXA, StatusCard.PENDENTE, LocalDate.of(2026, 4, 17), "Prof. Juliana", "Organizar ordem de atendimento.", agora.minusHours(4))
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
                CategoriaCard.ROTINA_AUXILIAR,
                List.of(
                        criarCard("Apoio na entrada dos alunos", "Organizacao do fluxo de entrada e recepcao dos estudantes.", CategoriaCard.ROTINA_AUXILIAR, PrioridadeCard.ALTA, StatusCard.EM_ANDAMENTO, null, "Equipe de Apoio", "Reforcar orientacao nos portoes.", agora.plusHours(7)),
                        criarCard("Organizacao dos corredores", "Acompanhar movimentacao no intervalo e troca de aulas.", CategoriaCard.ROTINA_AUXILIAR, PrioridadeCard.MEDIA, StatusCard.PENDENTE, null, "Auxiliares de Patio", "Atencao aos alunos do sexto ano.", agora.plusHours(8))
                )
        );
    }

    private void salvarCategoriaSeNecessario(CategoriaCard categoria, List<Card> cards) {
        if (cardRepository.existsByCategoria(categoria)) {
            return;
        }

        cardRepository.saveAll(cards);
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

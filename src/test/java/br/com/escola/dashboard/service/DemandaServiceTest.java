package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.DemandaRequestDTO;
import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandaServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private DemandaService demandaService;

    @Test
    void calcularProgressoPorSegmento_deveUsarDemandasConcluidasSobreTotal() {
        SegmentoCoordenacao segmento = SegmentoCoordenacao.EDUCACAO_INFANTIL;

        when(demandaRepository.countBySegmento(segmento)).thenReturn(4L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE)).thenReturn(1L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.EM_ANDAMENTO)).thenReturn(1L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CONCLUIDA)).thenReturn(2L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CANCELADA)).thenReturn(0L);

        DemandaService.ProgressoSegmento progresso = demandaService.calcularProgressoPorSegmento(segmento);

        assertEquals(50, progresso.percentual());
        assertEquals(2, progresso.concluidas());
        assertEquals(4, progresso.total());
    }

    @Test
    void calcularProgressoPorSegmento_semDemandas_deveRetornarZero() {
        SegmentoCoordenacao segmento = SegmentoCoordenacao.FUNDAMENTAL_1;

        when(demandaRepository.countBySegmento(segmento)).thenReturn(0L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE)).thenReturn(0L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.EM_ANDAMENTO)).thenReturn(0L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CONCLUIDA)).thenReturn(0L);
        when(demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CANCELADA)).thenReturn(0L);

        DemandaService.ProgressoSegmento progresso = demandaService.calcularProgressoPorSegmento(segmento);

        assertEquals(0, progresso.percentual());
        assertEquals(0, progresso.total());
    }

    @Test
    void criarDemanda_deveSalvarComoPendente() {
        DemandaRequestDTO request = new DemandaRequestDTO();
        request.setTitulo("Revisar plano");
        request.setDescricao("Detalhar entregas da semana");
        request.setSegmento(SegmentoCoordenacao.EDUCACAO_INFANTIL);
        request.setPrioridade(PrioridadeDemanda.ALTA);
        request.setDataPrazo(LocalDate.of(2026, 5, 25));

        when(demandaRepository.save(any(Demanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Demanda criada = demandaService.criarDemanda(request, "alissandra@escola.com");

        ArgumentCaptor<Demanda> captor = ArgumentCaptor.forClass(Demanda.class);
        verify(demandaRepository).save(captor.capture());

        Demanda salva = captor.getValue();
        assertEquals(StatusDemanda.PENDENTE, salva.getStatus());
        assertEquals(SegmentoCoordenacao.EDUCACAO_INFANTIL, salva.getSegmento());
        assertEquals(PrioridadeDemanda.ALTA, salva.getPrioridade());
        assertEquals("alissandra@escola.com", salva.getCriadaPor());
        assertNotNull(criada);
        assertEquals("Revisar plano", criada.getTitulo());
    }

    @Test
    void atualizarStatus_quandoDemandaNaoExiste_deveLancarExcecao() {
        when(demandaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> demandaService.atualizarStatus(99L, StatusDemanda.EM_ANDAMENTO));
    }

    @Test
    void atualizarStatusParaSegmento_quandoSegmentoDiferente_deveLancarExcecao() {
        Demanda demanda = new Demanda();
        demanda.setSegmento(SegmentoCoordenacao.FUNDAMENTAL_2);

        when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));

        assertThrows(IllegalArgumentException.class,
                () -> demandaService.atualizarStatusParaSegmento(
                        1L,
                        StatusDemanda.CONCLUIDA,
                        SegmentoCoordenacao.EDUCACAO_INFANTIL
                ));
    }

    @Test
    void atualizarStatusParaSegmento_quandoSegmentoCorreto_deveSalvar() {
        Demanda demanda = new Demanda();
        demanda.setSegmento(SegmentoCoordenacao.EDUCACAO_INFANTIL);
        demanda.setStatus(StatusDemanda.PENDENTE);

        when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
        when(demandaRepository.save(demanda)).thenReturn(demanda);

        Demanda atualizada = demandaService.atualizarStatusParaSegmento(
                1L,
                StatusDemanda.EM_ANDAMENTO,
                SegmentoCoordenacao.EDUCACAO_INFANTIL
        );

        assertEquals(StatusDemanda.EM_ANDAMENTO, atualizada.getStatus());
        assertEquals(true, atualizada.isVisualizadaPelaCoordenadora());
        verify(demandaRepository).save(demanda);
    }

    @Test
    void listarNovasPendentesPorSegmento_deveBuscarPendentesNaoVisualizadas() {
        SegmentoCoordenacao segmento = SegmentoCoordenacao.EDUCACAO_INFANTIL;
        Demanda demanda = new Demanda();

        when(demandaRepository.findBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalseOrderByDataPrazoAscDataCriacaoDesc(
                segmento,
                StatusDemanda.PENDENTE
        )).thenReturn(List.of(demanda));

        List<Demanda> resultado = demandaService.listarNovasPendentesPorSegmento(segmento);

        assertEquals(1, resultado.size());
    }

    @Test
    void marcarNovasPendentesComoVisualizadas_deveSalvarDemandasComoVistas() {
        SegmentoCoordenacao segmento = SegmentoCoordenacao.EDUCACAO_INFANTIL;
        Demanda primeira = new Demanda();
        Demanda segunda = new Demanda();

        when(demandaRepository.findBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalseOrderByDataPrazoAscDataCriacaoDesc(
                segmento,
                StatusDemanda.PENDENTE
        )).thenReturn(List.of(primeira, segunda));

        demandaService.marcarNovasPendentesComoVisualizadas(segmento);

        assertEquals(true, primeira.isVisualizadaPelaCoordenadora());
        assertEquals(true, segunda.isVisualizadaPelaCoordenadora());
        verify(demandaRepository).saveAll(List.of(primeira, segunda));
    }
}

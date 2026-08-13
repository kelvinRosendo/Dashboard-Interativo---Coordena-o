package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemanaEmFocoServiceTest {

    @Mock
    private SemanaEmFocoRepository repository;

    @InjectMocks
    private SemanaEmFocoService service;

    private SemanaEmFoco criarSemana(Long id, SegmentoCoordenacao segmento,
                                      LocalDate inicio, LocalDate fim, boolean ativa) {
        SemanaEmFoco s = new SemanaEmFoco();
        s.setId(id);
        s.setSegmento(segmento);
        s.setTitulo(segmento.getTitulo() + " em Foco");
        s.setDescricao("Descricao");
        s.setDataInicio(inicio);
        s.setDataFim(fim);
        s.setAtiva(ativa);
        return s;
    }

    @Test
    void buscarAtiva_deveRetornarUltimaAtiva() {
        SemanaEmFoco semana = new SemanaEmFoco();
        semana.setAtiva(true);

        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc()).thenReturn(List.of(semana));

        Optional<SemanaEmFoco> resultado = service.buscarAtiva();

        assertTrue(resultado.isPresent());
        assertEquals(semana, resultado.get());
    }

    @Test
    void buscarSemanaAtual_deveRetornarSemanaQueContemHoje() {
        LocalDate hoje = LocalDate.now();
        SemanaEmFoco semanaPassada = criarSemana(1L, SegmentoCoordenacao.EDUCACAO_INFANTIL,
                hoje.minusDays(20), hoje.minusDays(14), true);
        SemanaEmFoco semanaAtual = criarSemana(2L, SegmentoCoordenacao.FUNDAMENTAL_1,
                hoje.minusDays(2), hoje.plusDays(3), true);
        SemanaEmFoco semanaFutura = criarSemana(3L, SegmentoCoordenacao.FUNDAMENTAL_2,
                hoje.plusDays(7), hoje.plusDays(12), true);

        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc())
                .thenReturn(List.of(semanaPassada, semanaAtual, semanaFutura));

        Optional<SemanaEmFoco> resultado = service.buscarSemanaAtual();

        assertTrue(resultado.isPresent());
        assertEquals(semanaAtual.getId(), resultado.get().getId());
    }

    @Test
    void buscarSemanaAtual_deveRetornarMaisRecenteSeNenhumaContemHoje() {
        LocalDate hoje = LocalDate.now();
        SemanaEmFoco semanaPassada = criarSemana(1L, SegmentoCoordenacao.EDUCACAO_INFANTIL,
                hoje.minusDays(20), hoje.minusDays(14), true);

        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc())
                .thenReturn(List.of(semanaPassada));

        Optional<SemanaEmFoco> resultado = service.buscarSemanaAtual();

        assertTrue(resultado.isPresent());
        assertEquals(semanaPassada.getId(), resultado.get().getId());
    }

    @Test
    void buscarSemanaAtual_deveRetornarEmptySeNenhumaAtiva() {
        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc())
                .thenReturn(List.of());

        Optional<SemanaEmFoco> resultado = service.buscarSemanaAtual();

        assertFalse(resultado.isPresent());
    }

    @Test
    void buscarSemanaPorData_deveRetornarSemanaCorreta() {
        LocalDate data = LocalDate.of(2026, 8, 12);
        SemanaEmFoco semana1 = criarSemana(1L, SegmentoCoordenacao.EDUCACAO_INFANTIL,
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 7), false);
        SemanaEmFoco semana2 = criarSemana(2L, SegmentoCoordenacao.FUNDAMENTAL_1,
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 14), false);

        when(repository.findAll()).thenReturn(List.of(semana1, semana2));

        Optional<SemanaEmFoco> resultado = service.buscarSemanaPorData(data);

        assertTrue(resultado.isPresent());
        assertEquals(semana2.getId(), resultado.get().getId());
    }

    @Test
    void buscarSemanaPorData_deveDelegarParaBuscarSemanaAtualSeNull() {
        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc()).thenReturn(List.of());

        Optional<SemanaEmFoco> resultado = service.buscarSemanaPorData(null);

        assertFalse(resultado.isPresent());
    }

    @Test
    void listarAtivasPorSegmentos_deveFiltrarPorSegmento() {
        SegmentoCoordenacao seg = SegmentoCoordenacao.EDUCACAO_INFANTIL;
        when(repository.findByAtivaTrueAndSegmentoIn(anyList())).thenReturn(List.of());

        List<SemanaEmFoco> resultado = service.listarAtivasPorSegmentos(List.of(seg));

        assertNotNull(resultado);
        verify(repository).findByAtivaTrueAndSegmentoIn(anyList());
    }

    @Test
    void listarAtivasPorSegmentos_deveRetornarVazioSeSegmentosNulos() {
        List<SemanaEmFoco> resultado = service.listarAtivasPorSegmentos(null);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void salvar_quandoAtiva_deveDesativarOutrasAtivas() {
        SemanaEmFoco novaSemana = new SemanaEmFoco();
        novaSemana.setId(null);
        novaSemana.setAtiva(true);

        SemanaEmFoco semanaExistenteAtiva = new SemanaEmFoco();
        semanaExistenteAtiva.setId(10L);
        semanaExistenteAtiva.setAtiva(true);

        when(repository.findByAtivaTrueOrderByAtualizadoEmDesc()).thenReturn(List.of(semanaExistenteAtiva));
        when(repository.save(any(SemanaEmFoco.class))).thenAnswer(invocation -> {
            SemanaEmFoco arg = invocation.getArgument(0, SemanaEmFoco.class);
            return arg;
        });

        SemanaEmFoco salva = service.salvar(novaSemana);

        assertFalse(semanaExistenteAtiva.isAtiva());
        assertTrue(salva.isAtiva());
        verify(repository).save(semanaExistenteAtiva);
        verify(repository).save(novaSemana);
    }
}

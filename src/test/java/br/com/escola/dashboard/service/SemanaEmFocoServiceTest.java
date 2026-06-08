package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.SemanaEmFoco;
///import br.com.escola.dashboard.enums.PrioridadeDemanda;
///import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

///import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
///import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SemanaEmFocoServiceTest {

    @Mock
    private SemanaEmFocoRepository repository;

    @InjectMocks
    private SemanaEmFocoService service;

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
    void salvar_quandoAtiva_deveDesativarOutrasAtivas() {
        SemanaEmFoco novaSemana = new SemanaEmFoco();
        novaSemana.setId(null);
        novaSemana.setAtiva(true);

        SemanaEmFoco semanaExistenteAtiva = new SemanaEmFoco();
        semanaExistenteAtiva.setId(10L);
        semanaExistenteAtiva.setAtiva(true);

        when(repository.findAll()).thenReturn(List.of(semanaExistenteAtiva));
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

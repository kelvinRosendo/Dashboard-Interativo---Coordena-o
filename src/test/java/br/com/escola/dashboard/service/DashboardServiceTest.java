package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.DashboardDTO;
import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.repository.ImportacaoLogRepository;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import br.com.escola.dashboard.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PerfilService perfilService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private DemandaService demandaService;

    @Mock
    private SemanaEmFocoService semanaEmFocoService;

    @Mock
    private ComunicadoService comunicadoService;

    @Mock
    private AvisoService avisoService;

    @Mock
    private EventoService eventoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ImportacaoLogRepository importacaoLogRepository;

    @Mock
    private SemanaEmFocoRepository semanaEmFocoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Usuario criarUsuario(PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNome("Usuario Teste");
        usuario.setPerfil(perfil);
        usuario.setStatus(StatusUsuario.ATIVO);
        return usuario;
    }

    private Segmento criarSegmento(String slug, String titulo) {
        Segmento seg = new Segmento();
        seg.setId(1L);
        seg.setSlug(slug);
        seg.setTitulo(titulo);
        seg.setAtivo(true);
        return seg;
    }

    @Test
    void coletarDadosCoordenadora_deveRetornarDadosFiltrados() {
        Usuario coordenadora = criarUsuario(PerfilUsuario.COORDENADORA);
        Segmento seg = criarSegmento("fundamental-1", "Fundamental 1");

        when(perfilService.getSegmentosDoUsuario(coordenadora)).thenReturn(List.of(seg));
        when(demandaService.resumoPorSegmentos(anyList())).thenReturn(
                new DemandaService.ResumoDemandas(10, 3, 4, 2, 1, 7, 1, List.of())
        );
        when(demandaService.listarAtivasPorSegmentos(anyList())).thenReturn(List.of());
        when(demandaService.calcularProgressoPorSegmento(any())).thenReturn(
                new DemandaService.ProgressoSegmento(
                        SegmentoCoordenacao.FUNDAMENTAL_1, "fundamental-1", "Fundamental 1",
                        10, 3, 4, 2, 1, 7, 20
                )
        );
        when(semanaEmFocoService.listarAtivasPorSegmentos(anyList())).thenReturn(List.of());
        when(semanaEmFocoService.buscarSemanaAtual()).thenReturn(java.util.Optional.empty());
        when(comunicadoService.listarTodos()).thenReturn(List.of());
        when(avisoService.listarGlobaisEPorSegmentos(anyList())).thenReturn(List.of());
        when(eventoService.listarPorSegmentosECompartilhados(anyList())).thenReturn(List.of());
        when(semanaEmFocoRepository.countAtivasSemRelatorioPorSegmentos(anyList())).thenReturn(0L);

        DashboardDTO dashboard = dashboardService.coletarDadosCoordenadora(coordenadora);

        assertNotNull(dashboard);
        assertEquals(PerfilUsuario.COORDENADORA, dashboard.perfil());
        assertEquals("Usuario Teste", dashboard.nome());
        assertEquals(10, dashboard.indicadores().totalDemandas());
        assertEquals(3, dashboard.pendencias().demandasPendentes());
        verify(demandaService).resumoPorSegmentos(anyList());
        verify(demandaService).listarAtivasPorSegmentos(anyList());
        verify(avisoService).listarGlobaisEPorSegmentos(anyList());
        verify(eventoService).listarPorSegmentosECompartilhados(anyList());
    }

    @Test
    void coletarDadosAdmin_deveRetornarTodosDados() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        Segmento seg1 = criarSegmento("fundamental-1", "Fundamental 1");
        Segmento seg2 = criarSegmento("ensino-medio", "Ensino Medio");

        when(perfilService.getSegmentosDoUsuario(admin)).thenReturn(List.of(seg1, seg2));
        when(demandaService.listarTodasParaAdmin()).thenReturn(List.of());
        when(demandaService.resumoGeral()).thenReturn(
                new DemandaService.ResumoDemandas(20, 5, 8, 5, 2, 13, 3, List.of())
        );
        when(demandaService.calcularProgressoPorSegmento(any())).thenReturn(
                new DemandaService.ProgressoSegmento(
                        SegmentoCoordenacao.FUNDAMENTAL_1, "fundamental-1", "Fundamental 1",
                        10, 3, 4, 2, 1, 7, 20
                )
        );
        when(semanaEmFocoService.buscarSemanaAtual()).thenReturn(java.util.Optional.empty());
        when(semanaEmFocoService.buscarAtiva()).thenReturn(java.util.Optional.empty());
        when(comunicadoService.listarTodos()).thenReturn(List.of());
        when(avisoService.listarTodos()).thenReturn(List.of());
        when(eventoService.listarTodos()).thenReturn(List.of());
        when(usuarioRepository.countByStatus(StatusUsuario.ATIVO)).thenReturn(5L);
        when(usuarioRepository.countByStatus(StatusUsuario.PENDENTE)).thenReturn(2L);
        when(usuarioRepository.count()).thenReturn(10L);
        when(importacaoLogRepository.count()).thenReturn(3L);
        when(semanaEmFocoRepository.countAtivasSemRelatorio()).thenReturn(2L);

        DashboardDTO dashboard = dashboardService.coletarDadosAdmin(admin);

        assertNotNull(dashboard);
        assertEquals(PerfilUsuario.ADMIN, dashboard.perfil());
        assertEquals(20, dashboard.indicadores().totalDemandas());
        assertEquals(5, dashboard.indicadores().usuariosAtivos());
        assertEquals(2, dashboard.indicadores().usuariosPendentes());
        assertEquals(10, dashboard.indicadores().totalUsuarios());
        assertEquals(3, dashboard.indicadores().importacoesRealizadas());
        assertEquals(2, dashboard.segmentos().size());
    }

    @Test
    void coletarDadosViceDiretora_deveRetornarTodosDados() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        Segmento seg = criarSegmento("fundamental-1", "Fundamental 1");

        when(perfilService.getSegmentosDoUsuario(vice)).thenReturn(List.of(seg));
        when(demandaService.resumoPorSegmentos(anyList())).thenReturn(
                new DemandaService.ResumoDemandas(15, 4, 6, 3, 2, 10, 2, List.of())
        );
        when(demandaService.listarAtivasPorSegmentos(anyList())).thenReturn(List.of());
        when(demandaService.calcularProgressoPorSegmento(any())).thenReturn(
                new DemandaService.ProgressoSegmento(
                        SegmentoCoordenacao.FUNDAMENTAL_1, "fundamental-1", "Fundamental 1",
                        15, 4, 6, 3, 2, 10, 20
                )
        );
        when(semanaEmFocoService.buscarSemanaAtual()).thenReturn(java.util.Optional.empty());
        when(semanaEmFocoService.buscarAtiva()).thenReturn(java.util.Optional.empty());
        when(comunicadoService.listarTodos()).thenReturn(List.of());
        when(avisoService.listarTodos()).thenReturn(List.of());
        when(eventoService.listarTodos()).thenReturn(List.of());
        when(semanaEmFocoRepository.countAtivasSemRelatorio()).thenReturn(1L);

        DashboardDTO dashboard = dashboardService.coletarDadosViceDiretora(vice);

        assertNotNull(dashboard);
        assertEquals(PerfilUsuario.VICE_DIRETORA, dashboard.perfil());
        assertEquals(15, dashboard.indicadores().totalDemandas());
        assertEquals(1, dashboard.segmentos().size());
    }

    @Test
    void coletarDados_deveRetornarNullParaUsuarioNulo() {
        DashboardDTO result = dashboardService.coletarDados(null);
        assertNull(result);
    }

    @Test
    void coletarDados_deveDespacharPorPerfil() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        when(perfilService.getSegmentosDoUsuario(admin)).thenReturn(List.of());
        when(demandaService.listarTodasParaAdmin()).thenReturn(List.of());
        when(demandaService.resumoGeral()).thenReturn(
                new DemandaService.ResumoDemandas(0, 0, 0, 0, 0, 0, 0, List.of())
        );
        when(semanaEmFocoService.buscarSemanaAtual()).thenReturn(java.util.Optional.empty());
        when(semanaEmFocoService.buscarAtiva()).thenReturn(java.util.Optional.empty());
        when(comunicadoService.listarTodos()).thenReturn(List.of());
        when(avisoService.listarTodos()).thenReturn(List.of());
        when(eventoService.listarTodos()).thenReturn(List.of());
        when(usuarioRepository.countByStatus(any())).thenReturn(0L);
        when(usuarioRepository.count()).thenReturn(0L);
        when(importacaoLogRepository.count()).thenReturn(0L);
        when(semanaEmFocoRepository.countAtivasSemRelatorio()).thenReturn(0L);

        DashboardDTO dashboard = dashboardService.coletarDados(admin);

        assertNotNull(dashboard);
        assertEquals(PerfilUsuario.ADMIN, dashboard.perfil());
    }

    @Test
    void coletarDadosCoordenadora_naoDeveIncluirDadosDeOutroSegmento() {
        Usuario coordenadora = criarUsuario(PerfilUsuario.COORDENADORA);
        Segmento segInfantil = criarSegmento("educacao-infantil", "Educacao Infantil");

        when(perfilService.getSegmentosDoUsuario(coordenadora)).thenReturn(List.of(segInfantil));
        when(demandaService.resumoPorSegmentos(anyList())).thenReturn(
                new DemandaService.ResumoDemandas(5, 2, 2, 1, 0, 4, 0, List.of())
        );
        when(demandaService.listarAtivasPorSegmentos(anyList())).thenReturn(List.of());
        when(demandaService.calcularProgressoPorSegmento(any())).thenReturn(
                new DemandaService.ProgressoSegmento(
                        SegmentoCoordenacao.EDUCACAO_INFANTIL, "educacao-infantil", "Educacao Infantil",
                        5, 2, 2, 1, 0, 4, 20
                )
        );
        when(semanaEmFocoService.listarAtivasPorSegmentos(anyList())).thenReturn(List.of());
        when(semanaEmFocoService.buscarSemanaAtual()).thenReturn(java.util.Optional.empty());
        when(comunicadoService.listarTodos()).thenReturn(List.of());
        when(avisoService.listarGlobaisEPorSegmentos(anyList())).thenReturn(List.of());
        when(eventoService.listarPorSegmentosECompartilhados(anyList())).thenReturn(List.of());
        when(semanaEmFocoRepository.countAtivasSemRelatorioPorSegmentos(anyList())).thenReturn(1L);

        DashboardDTO dashboard = dashboardService.coletarDadosCoordenadora(coordenadora);

        assertNotNull(dashboard);
        assertEquals(1, dashboard.segmentos().size());
        assertEquals("educacao-infantil", dashboard.segmentos().get(0).slug());

        verify(demandaService).resumoPorSegmentos(argThat(segmentos ->
                segmentos.size() == 1 && segmentos.contains(SegmentoCoordenacao.EDUCACAO_INFANTIL)
        ));
    }
}

package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.repository.SegmentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Sprint83AuthorizationTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SegmentoRepository segmentoRepository;

    @InjectMocks
    private PerfilService perfilService;

    private Usuario criarUsuario(PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNome("Usuario Teste");
        usuario.setPerfil(perfil);
        usuario.setStatus(StatusUsuario.ATIVO);
        return usuario;
    }

    private Segmento criarSegmento(Long id, String slug, String titulo) {
        Segmento seg = new Segmento();
        seg.setId(id);
        seg.setSlug(slug);
        seg.setTitulo(titulo);
        seg.setAtivo(true);
        return seg;
    }

    // =========================================================================
    // BUG-001: Vice-Diretora access must be profile-based, not email-based
    // =========================================================================

    @Test
    void perfilService_isViceDiretora_deveRetornarTrueParaPerfilViceDiretora() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        assertTrue(perfilService.isViceDiretora(vice));
    }

    @Test
    void perfilService_isViceDiretora_deveRetornarFalseParaCoordenadora() {
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertFalse(perfilService.isViceDiretora(coord));
    }

    @Test
    void perfilService_isViceDiretora_deveRetornarFalseParaAdmin() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertFalse(perfilService.isViceDiretora(admin));
    }

    @Test
    void perfilService_getDashboardRedirect_deveRetornarViceDiretora() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        assertEquals("/vice-diretora", perfilService.getDashboardRedirect(vice));
    }

    @Test
    void perfilService_getDashboardRedirect_deveRetornarAdmin() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertEquals("/admin", perfilService.getDashboardRedirect(admin));
    }

    @Test
    void perfilService_getDashboardRedirect_deveRetornarCoordenadora() {
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertEquals("/coordenadora/dashboard", perfilService.getDashboardRedirect(coord));
    }

    // =========================================================================
    // BUG-002: Coordenadora dashboard access must check profile, not just status
    // =========================================================================

    @Test
    void perfilService_isCoordenadora_deveRetornarTrueParaPerfilCoordenadora() {
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertTrue(perfilService.isCoordenadora(coord));
    }

    @Test
    void perfilService_isCoordenadora_deveRetornarFalseParaViceDiretora() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        assertFalse(perfilService.isCoordenadora(vice));
    }

    @Test
    void perfilService_isCoordenadora_deveRetornarFalseParaAdmin() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertFalse(perfilService.isCoordenadora(admin));
    }

    @Test
    void perfilService_isAdmin_deveRetornarTrueApenasParaAdmin() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertTrue(perfilService.isAdmin(admin));

        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertFalse(perfilService.isAdmin(coord));

        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        assertFalse(perfilService.isAdmin(vice));
    }

    // =========================================================================
    // BUG-003 + BUG-005: Profile-based authorization (email does not override)
    // =========================================================================

    @Test
    void perfilService_getSegmentosDoUsuario_adminDeveReceberTodosSegmentos() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        Segmento seg1 = criarSegmento(1L, "fundamental-1", "Fundamental 1");
        Segmento seg2 = criarSegmento(2L, "ensino-medio", "Ensino Medio");

        when(segmentoRepository.findByAtivoTrueOrderByTitulo()).thenReturn(List.of(seg1, seg2));

        List<Segmento> segmentos = perfilService.getSegmentosDoUsuario(admin);

        assertEquals(2, segmentos.size());
        verify(segmentoRepository).findByAtivoTrueOrderByTitulo();
        verify(usuarioService, never()).buscarSegmentosDoUsuario(any());
    }

    @Test
    void perfilService_getSegmentosDoUsuario_viceDiretoraDeveReceberTodosSegmentos() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        Segmento seg1 = criarSegmento(1L, "fundamental-1", "Fundamental 1");

        when(segmentoRepository.findByAtivoTrueOrderByTitulo()).thenReturn(List.of(seg1));

        List<Segmento> segmentos = perfilService.getSegmentosDoUsuario(vice);

        assertEquals(1, segmentos.size());
        verify(segmentoRepository).findByAtivoTrueOrderByTitulo();
        verify(usuarioService, never()).buscarSegmentosDoUsuario(any());
    }

    @Test
    void perfilService_getSegmentosDoUsuario_coordenadoraDeveReceberApenasSegmentosVinculados() {
        Segmento segFII = criarSegmento(1L, "fundamental-1", "Fundamental 1");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarSegmentosDoUsuario(coord.getId())).thenReturn(List.of(segFII));

        List<Segmento> segmentos = perfilService.getSegmentosDoUsuario(coord);

        assertEquals(1, segmentos.size());
        assertEquals("fundamental-1", segmentos.get(0).getSlug());
        verify(usuarioService).buscarSegmentosDoUsuario(coord.getId());
        verify(segmentoRepository, never()).findByAtivoTrueOrderByTitulo();
    }

    @Test
    void perfilService_getSegmentosDoUsuario_coordenadoraNaoDeveReceberSegmentosDeOutros() {
        Segmento segEI = criarSegmento(1L, "educacao-infantil", "Educacao Infantil");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarSegmentosDoUsuario(coord.getId())).thenReturn(List.of(segEI));

        List<Segmento> segmentos = perfilService.getSegmentosDoUsuario(coord);

        assertEquals(1, segmentos.size());
        assertEquals("educacao-infantil", segmentos.get(0).getSlug());
        assertFalse(segmentos.stream().anyMatch(s -> s.getSlug().equals("fundamental-1")));
    }

    // =========================================================================
    // isAdminOrViceDiretora
    // =========================================================================

    @Test
    void perfilService_isAdminOrViceDiretora_deveRetornarTrueParaAdmin() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertTrue(perfilService.isAdminOrViceDiretora(admin));
    }

    @Test
    void perfilService_isAdminOrViceDiretora_deveRetornarTrueParaViceDiretora() {
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        assertTrue(perfilService.isAdminOrViceDiretora(vice));
    }

    @Test
    void perfilService_isAdminOrViceDiretora_deveRetornarFalseParaCoordenadora() {
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertFalse(perfilService.isAdminOrViceDiretora(coord));
    }

    // =========================================================================
    // Null safety
    // =========================================================================

    @Test
    void perfilService_nullSafety() {
        assertFalse(perfilService.isAdmin(null));
        assertFalse(perfilService.isViceDiretora(null));
        assertFalse(perfilService.isCoordenadora(null));
        assertFalse(perfilService.isAdminOrViceDiretora(null));
        assertEquals("/login", perfilService.getDashboardRedirect(null));
    }

    @Test
    void perfilService_getSegmentosDoUsuario_nullDeveRetornarListaVazia() {
        List<Segmento> result = perfilService.getSegmentosDoUsuario(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // Segment isolation: coordenadora with FII should NOT see EI, FI, EM data
    // =========================================================================

    @Test
    void perfilService_coordenadoraComFiiNaoDeveVerSegmentosEI() {
        Segmento segFII = criarSegmento(1L, "fundamental-1", "Fundamental 1");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarSegmentosDoUsuario(coord.getId())).thenReturn(List.of(segFII));

        List<Segmento> segmentos = perfilService.getSegmentosDoUsuario(coord);

        assertEquals(1, segmentos.size());
        assertFalse(segmentos.stream().anyMatch(s -> s.getSlug().equals("educacao-infantil")));
        assertFalse(segmentos.stream().anyMatch(s -> s.getSlug().equals("fundamental-2")));
        assertFalse(segmentos.stream().anyMatch(s -> s.getSlug().equals("ensino-medio")));
    }
}

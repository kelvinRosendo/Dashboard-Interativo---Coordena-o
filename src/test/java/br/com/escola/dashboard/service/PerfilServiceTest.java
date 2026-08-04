package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private br.com.escola.dashboard.repository.SegmentoRepository segmentoRepository;

    @InjectMocks
    private PerfilService perfilService;

    @Test
    void isAdmin_deveRetornarTrueQuandoPerfilEAdmin() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.ADMIN);

        assertTrue(perfilService.isAdmin(usuario));
    }

    @Test
    void isAdmin_deveRetornarFalseQuandoPerfilNaoEAdmin() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.COORDENADORA);

        assertFalse(perfilService.isAdmin(usuario));
    }

    @Test
    void isViceDiretora_deveRetornarTrueQuandoPerfilEViceDiretora() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.VICE_DIRETORA);

        assertTrue(perfilService.isViceDiretora(usuario));
    }

    @Test
    void isCoordenadora_deveRetornarTrueQuandoPerfilECoordenadora() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.COORDENADORA);

        assertTrue(perfilService.isCoordenadora(usuario));
    }

    @Test
    void isAdmin_deveRetornarFalseQuandoUsuarioENull() {
        assertFalse(perfilService.isAdmin(null));
    }

    @Test
    void getDashboardRedirect_deveRetornarAdminParaAdmin() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.ADMIN);

        assertEquals("/admin", perfilService.getDashboardRedirect(usuario));
    }

    @Test
    void getDashboardRedirect_deveRetornarViceDiretoraParaViceDiretora() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.VICE_DIRETORA);

        assertEquals("/vice-diretora", perfilService.getDashboardRedirect(usuario));
    }

    @Test
    void getDashboardRedirect_deveRetornarCoordenadoraParaCoordenadora() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(PerfilUsuario.COORDENADORA);

        assertEquals("/coordenadora/dashboard", perfilService.getDashboardRedirect(usuario));
    }

    @Test
    void getDashboardRedirect_deveRetornarLoginParaNull() {
        assertEquals("/login", perfilService.getDashboardRedirect(null));
    }
}

package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.repository.SegmentoRepository;
import br.com.escola.dashboard.repository.UsuarioRepository;
import br.com.escola.dashboard.repository.UsuarioSegmentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SegmentoRepository segmentoRepository;

    @Mock
    private UsuarioSegmentoRepository usuarioSegmentoRepository;

    private UsuarioService createService(String adminEmails) {
        return new UsuarioService(usuarioRepository, segmentoRepository, usuarioSegmentoRepository, adminEmails);
    }

    @Test
    void buscarOuCriarPorGoogle_deveCriarNovoUsuarioComPerfilCoordenadora() {
        UsuarioService service = createService("");
        when(usuarioRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario result = service.buscarOuCriarPorGoogle(
                "google123", "novo@email.com", "Novo Usuario", "http://foto.jpg");

        assertNotNull(result);
        assertEquals("novo@email.com", result.getEmail());
        assertEquals("Novo Usuario", result.getNome());
        assertEquals(PerfilUsuario.COORDENADORA, result.getPerfil());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void buscarOuCriarPorGoogle_deveCriarAdminSeEmailAutorizado() {
        UsuarioService service = createService("admin@email.com");
        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario result = service.buscarOuCriarPorGoogle(
                "google456", "admin@email.com", "Admin User", null);

        assertNotNull(result);
        assertEquals(PerfilUsuario.ADMIN, result.getPerfil());
    }

    @Test
    void buscarOuCriarPorGoogle_deveAtualizarUsuarioExistente() {
        UsuarioService service = createService("");
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("existente@email.com");
        existente.setNome("Nome Antigo");
        existente.setPerfil(PerfilUsuario.COORDENADORA);

        when(usuarioRepository.findByEmail("existente@email.com")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = service.buscarOuCriarPorGoogle(
                "google789", "existente@email.com", "Nome Novo", "http://nova-foto.jpg");

        assertEquals("Nome Novo", result.getNome());
        assertEquals("http://nova-foto.jpg", result.getFotoUrl());
    }

    @Test
    void buscarPorEmail_deveRetornarUsuarioQuandoExiste() {
        UsuarioService service = createService("");
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        Usuario result = service.buscarPorEmail("teste@email.com");

        assertNotNull(result);
        assertEquals("teste@email.com", result.getEmail());
    }

    @Test
    void buscarPorEmail_deveRetornarNullQuandoNaoExiste() {
        UsuarioService service = createService("");
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        Usuario result = service.buscarPorEmail("naoexiste@email.com");

        assertNull(result);
    }
}

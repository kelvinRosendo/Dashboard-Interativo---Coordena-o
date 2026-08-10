package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerLoopTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    private OAuth2User criarOAuth2User(String email) {
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(email);
        return oauth2User;
    }

    private Usuario criarUsuario(PerfilUsuario perfil, StatusUsuario status) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNome("Usuario Teste");
        usuario.setPerfil(perfil);
        usuario.setStatus(status);
        return usuario;
    }

    // =========================================================================
    // REQUISITO 1: Usuario PENDENTE autenticado NAO deve ser redirecionado para /admin
    // =========================================================================

    @Test
    void usuarioPendente_autenticado_deveRenderizarLoginComErro() {
        OAuth2User oauth2User = criarOAuth2User("pendente@email.com");
        Usuario usuario = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.PENDENTE);
        when(usuarioService.buscarPorEmail("pendente@email.com")).thenReturn(usuario);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("login", resultado);
        assertFalse(model.containsAttribute("redirectUrl"));
        assertNotNull(model.getAttribute("mensagemErro"));
        verify(usuarioService).buscarPorEmail("pendente@email.com");
    }

    // =========================================================================
    // REQUISITO 2: Usuario BLOQUEADO autenticado NAO deve ser redirecionado para /admin
    // =========================================================================

    @Test
    void usuarioBloqueado_autenticado_deveRenderizarLoginComErro() {
        OAuth2User oauth2User = criarOAuth2User("bloqueado@email.com");
        Usuario usuario = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.BLOQUEADO);
        when(usuarioService.buscarPorEmail("bloqueado@email.com")).thenReturn(usuario);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("login", resultado);
        assertNotNull(model.getAttribute("mensagemErro"));
        verify(usuarioService).buscarPorEmail("bloqueado@email.com");
    }

    // =========================================================================
    // REQUISITO 3: Usuario ATIVO ADMIN deve ir para /dashboard
    // =========================================================================

    @Test
    void usuarioAtivoAdmin_deveRedirecionarParaDashboard() {
        OAuth2User oauth2User = criarOAuth2User("admin@email.com");
        Usuario usuario = criarUsuario(PerfilUsuario.ADMIN, StatusUsuario.ATIVO);
        when(usuarioService.buscarPorEmail("admin@email.com")).thenReturn(usuario);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("redirect:/dashboard", resultado);
        verify(usuarioService).buscarPorEmail("admin@email.com");
    }

    // =========================================================================
    // REQUISITO 4: Usuario ATIVO VICE_DIRETORA deve chegar ao /vice-diretora
    // =========================================================================

    @Test
    void usuarioAtivoViceDiretora_deveRedirecionarParaDashboard() {
        OAuth2User oauth2User = criarOAuth2User("vice@email.com");
        Usuario usuario = criarUsuario(PerfilUsuario.VICE_DIRETORA, StatusUsuario.ATIVO);
        when(usuarioService.buscarPorEmail("vice@email.com")).thenReturn(usuario);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("redirect:/dashboard", resultado);
        verify(usuarioService).buscarPorEmail("vice@email.com");
    }

    // =========================================================================
    // REQUISITO 5: Usuario ATIVO COORDENADORA deve chegar ao /coordenadora/dashboard
    // =========================================================================

    @Test
    void usuarioAtivoCoordenadora_deveRedirecionarParaDashboard() {
        OAuth2User oauth2User = criarOAuth2User("coord@email.com");
        Usuario usuario = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.ATIVO);
        when(usuarioService.buscarPorEmail("coord@email.com")).thenReturn(usuario);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("redirect:/dashboard", resultado);
        verify(usuarioService).buscarPorEmail("coord@email.com");
    }

    // =========================================================================
    // REQUISITO 6: Usuario nao autenticado continua podendo acessar /login
    // =========================================================================

    @Test
    void usuarioNaoAutenticado_deveRenderizarLogin() {
        Model model = new ConcurrentModel();
        String resultado = authController.login(null, request, model);

        assertEquals("login", resultado);
        assertNotNull(model.getAttribute("googleConfigurado"));
        verify(usuarioService, never()).buscarPorEmail(anyString());
    }

    // =========================================================================
    // REQUISITO 7: Nenhum cenario deve produzir redirect circular
    // =========================================================================

    @Test
    void nenhumCenarioDeveProduzirRedirectCircular() {
        // Cenario 1: PENDENTE nao deve redirecionar
        OAuth2User pendente = criarOAuth2User("pendente@email.com");
        Usuario usuarioPendente = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.PENDENTE);
        when(usuarioService.buscarPorEmail("pendente@email.com")).thenReturn(usuarioPendente);

        Model model1 = new ConcurrentModel();
        String r1 = authController.login(pendente, request, model1);
        assertFalse(r1.startsWith("redirect:"), "PENDENTE nao deve redirecionar");

        // Cenario 2: BLOQUEADO nao deve redirecionar
        OAuth2User bloqueado = criarOAuth2User("bloqueado@email.com");
        Usuario usuarioBloqueado = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.BLOQUEADO);
        when(usuarioService.buscarPorEmail("bloqueado@email.com")).thenReturn(usuarioBloqueado);

        Model model2 = new ConcurrentModel();
        String r2 = authController.login(bloqueado, request, model2);
        assertFalse(r2.startsWith("redirect:"), "BLOQUEADO nao deve redirecionar");

        // Cenario 3: usuario nao encontrado redireciona para /logout (limpa sessao)
        OAuth2User fantasma = criarOAuth2User("fantasma@email.com");
        when(usuarioService.buscarPorEmail("fantasma@email.com")).thenReturn(null);

        Model model3 = new ConcurrentModel();
        String r3 = authController.login(fantasma, request, model3);
        assertEquals("redirect:/logout", r3, "usuario nao encontrado deve redirecionar para /logout");

        // Cenario 4: ATIVO deve redirecionar para /dashboard (unico redirect, sem loop)
        OAuth2User ativo = criarOAuth2User("ativo@email.com");
        Usuario usuarioAtivo = criarUsuario(PerfilUsuario.COORDENADORA, StatusUsuario.ATIVO);
        when(usuarioService.buscarPorEmail("ativo@email.com")).thenReturn(usuarioAtivo);

        Model model4 = new ConcurrentModel();
        String r4 = authController.login(ativo, request, model4);
        assertEquals("redirect:/dashboard", r4, "ATIVO deve redirecionar para /dashboard");
    }

    // =========================================================================
    // Cenarios extras: Usuario nao encontrado no banco
    // =========================================================================

    @Test
    void usuarioNaoEncontradoNoBanco_deveRedirecionarParaLogout() {
        OAuth2User oauth2User = criarOAuth2User("naoexiste@email.com");
        when(usuarioService.buscarPorEmail("naoexiste@email.com")).thenReturn(null);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("redirect:/logout", resultado);
        verify(usuarioService).buscarPorEmail("naoexiste@email.com");
    }

    @Test
    void usuarioAtualNull_deveRedirecionarParaLogout() {
        OAuth2User oauth2User = criarOAuth2User("fantasma@email.com");
        when(usuarioService.buscarPorEmail("fantasma@email.com")).thenReturn(null);

        Model model = new ConcurrentModel();
        String resultado = authController.login(oauth2User, request, model);

        assertEquals("redirect:/logout", resultado,
                "usuarioAtual == null deve redirecionar para /logout (limpa sessao orfa)");
    }
}

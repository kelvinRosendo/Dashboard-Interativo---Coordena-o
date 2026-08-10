package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private UsuarioService usuarioService;

    private AdminAuthService createService(String authorizedEmails) {
        return new AdminAuthService(authorizedEmails, usuarioService);
    }

    private Usuario criarUsuario(PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNome("Usuario Teste");
        usuario.setPerfil(perfil);
        usuario.setStatus(StatusUsuario.ATIVO);
        return usuario;
    }

    private OAuth2User criarOAuth2User(String email) {
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(email);
        return oauth2User;
    }

    // =========================================================================
    // isAdminEmailAuthorized - email whitelist (still used for user creation)
    // =========================================================================

    @Test
    void isAdminEmailAuthorized_deveRetornarTrueParaEmailNaLista() {
        AdminAuthService service = createService("alissandra@colegiosatelite.com.br");
        assertTrue(service.isAdminEmailAuthorized("alissandra@colegiosatelite.com.br"));
    }

    @Test
    void isAdminEmailAuthorized_deveRetornarFalseParaEmailForaDaLista() {
        AdminAuthService service = createService("alissandra@colegiosatelite.com.br");
        assertFalse(service.isAdminEmailAuthorized("qualquer@email.com"));
    }

    @Test
    void isAdminEmailAuthorized_deveRetornarFalseParaEmailNulo() {
        AdminAuthService service = createService("alissandra@colegiosatelite.com.br");
        assertFalse(service.isAdminEmailAuthorized(null));
    }

    @Test
    void isAdminEmailAuthorized_deveRetornarFalseParaEmailEmBranco() {
        AdminAuthService service = createService("alissandra@colegiosatelite.com.br");
        assertFalse(service.isAdminEmailAuthorized(""));
    }

    @Test
    void isAdminEmailAuthorized_deveAceitarMultiplosEmails() {
        AdminAuthService service = createService("admin1@test.com, admin2@test.com");
        assertTrue(service.isAdminEmailAuthorized("admin1@test.com"));
        assertTrue(service.isAdminEmailAuthorized("admin2@test.com"));
        assertFalse(service.isAdminEmailAuthorized("admin3@test.com"));
    }

    // =========================================================================
    // hasPerfil(OAuth2User, PerfilUsuario) tests
    // =========================================================================

    @Test
    void hasPerfil_deveRetornarTrueQuandoPerfilCorresponde() {
        AdminAuthService service = createService("");
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        when(usuarioService.buscarPorEmail("admin@email.com")).thenReturn(admin);

        OAuth2User oauth2User = criarOAuth2User("admin@email.com");

        assertTrue(service.hasPerfil(oauth2User, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_deveRetornarFalseQuandoPerfilNaoCorresponde() {
        AdminAuthService service = createService("");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarPorEmail("coord@email.com")).thenReturn(coord);

        OAuth2User oauth2User = criarOAuth2User("coord@email.com");

        assertFalse(service.hasPerfil(oauth2User, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_deveRetornarFalseParaUsuarioNaoCadastrado() {
        AdminAuthService service = createService("");
        when(usuarioService.buscarPorEmail("naoexiste@email.com")).thenReturn(null);

        OAuth2User oauth2User = criarOAuth2User("naoexiste@email.com");

        assertFalse(service.hasPerfil(oauth2User, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_deveRetornarFalseParaOAuth2UserNulo() {
        AdminAuthService service = createService("");
        assertFalse(service.hasPerfil((OAuth2User) null, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_deveRetornarFalseParaPerfilNulo() {
        AdminAuthService service = createService("");
        OAuth2User oauth2User = mock(OAuth2User.class);
        assertFalse(service.hasPerfil(oauth2User, null));
    }

    // =========================================================================
    // hasPerfil(Usuario, PerfilUsuario) tests
    // =========================================================================

    @Test
    void hasPerfil_usuario_deveRetornarTrueQuandoPerfilCorresponde() {
        AdminAuthService service = createService("");
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        assertTrue(service.hasPerfil(admin, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_usuario_deveRetornarFalseQuandoPerfilNaoCorresponde() {
        AdminAuthService service = createService("");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        assertFalse(service.hasPerfil(coord, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfil_usuario_deveRetornarFalseParaNull() {
        AdminAuthService service = createService("");
        assertFalse(service.hasPerfil((Usuario) null, PerfilUsuario.ADMIN));
    }

    // =========================================================================
    // hasPerfilOrAdmin tests
    // =========================================================================

    @Test
    void hasPerfilOrAdmin_deveRetornarTrueParaAdmin() {
        AdminAuthService service = createService("");
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        when(usuarioService.buscarPorEmail("admin@email.com")).thenReturn(admin);

        OAuth2User oauth2User = criarOAuth2User("admin@email.com");

        assertTrue(service.hasPerfilOrAdmin(oauth2User, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfilOrAdmin_deveRetornarTrueParaAdminAcessandoQualquerPerfil() {
        AdminAuthService service = createService("");
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        when(usuarioService.buscarPorEmail("admin@email.com")).thenReturn(admin);

        OAuth2User oauth2User = criarOAuth2User("admin@email.com");

        assertTrue(service.hasPerfilOrAdmin(oauth2User, PerfilUsuario.VICE_DIRETORA));
        assertTrue(service.hasPerfilOrAdmin(oauth2User, PerfilUsuario.COORDENADORA));
    }

    @Test
    void hasPerfilOrAdmin_deveRetornarFalseParaCoordenadora() {
        AdminAuthService service = createService("");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarPorEmail("coord@email.com")).thenReturn(coord);

        OAuth2User oauth2User = criarOAuth2User("coord@email.com");

        assertFalse(service.hasPerfilOrAdmin(oauth2User, PerfilUsuario.ADMIN));
    }

    @Test
    void hasPerfilOrAdmin_deveRetornarFalseParaNull() {
        AdminAuthService service = createService("");
        assertFalse(service.hasPerfilOrAdmin(null, PerfilUsuario.ADMIN));
    }

    // =========================================================================
    // isAdmin - requires both email whitelist AND profile ADMIN
    // =========================================================================

    @Test
    void isAdmin_deveRetornarTrueParaEmailAutorizadoEPerfilAdmin() {
        AdminAuthService service = createService("admin@test.com");
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN);
        admin.setEmail("admin@test.com");
        when(usuarioService.buscarPorEmail("admin@test.com")).thenReturn(admin);

        OAuth2User oauth2User = criarOAuth2User("admin@test.com");

        assertTrue(service.isAdmin(oauth2User));
    }

    @Test
    void isAdmin_deveRetornarFalseParaEmailAutorizadoMasPerfilCoordenadora() {
        AdminAuthService service = createService("coord@test.com");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        coord.setEmail("coord@test.com");
        when(usuarioService.buscarPorEmail("coord@test.com")).thenReturn(coord);

        OAuth2User oauth2User = criarOAuth2User("coord@test.com");

        assertFalse(service.isAdmin(oauth2User));
    }

    @Test
    void isAdmin_deveRetornarFalseParaEmailNaoAutorizado() {
        AdminAuthService service = createService("admin@test.com");

        OAuth2User oauth2User = criarOAuth2User("qualquer@email.com");

        assertFalse(service.isAdmin(oauth2User));
    }

    @Test
    void isAdmin_deveRetornarFalseParaNull() {
        AdminAuthService service = createService("admin@test.com");
        assertFalse(service.isAdmin(null));
    }

    // =========================================================================
    // isViceDiretora / isCoordenadora
    // =========================================================================

    @Test
    void isViceDiretora_deveRetornarTrueParaPerfilCorreto() {
        AdminAuthService service = createService("");
        Usuario vice = criarUsuario(PerfilUsuario.VICE_DIRETORA);
        when(usuarioService.buscarPorEmail("vice@email.com")).thenReturn(vice);

        OAuth2User oauth2User = criarOAuth2User("vice@email.com");

        assertTrue(service.isViceDiretora(oauth2User));
    }

    @Test
    void isCoordenadora_deveRetornarTrueParaPerfilCorreto() {
        AdminAuthService service = createService("");
        Usuario coord = criarUsuario(PerfilUsuario.COORDENADORA);
        when(usuarioService.buscarPorEmail("coord@email.com")).thenReturn(coord);

        OAuth2User oauth2User = criarOAuth2User("coord@email.com");

        assertTrue(service.isCoordenadora(oauth2User));
    }
}

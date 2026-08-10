package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminAuthService {

    private final Set<String> authorizedEmails;
    private final UsuarioService usuarioService;

    public AdminAuthService(@Value("${app.admin.authorized-emails}") String authorizedEmailsConfig,
                            UsuarioService usuarioService) {
        this.authorizedEmails = parseEmails(authorizedEmailsConfig);
        this.usuarioService = usuarioService;
    }

    public boolean isAdminEmailAuthorized(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return authorizedEmails.contains(email.trim().toLowerCase());
    }

    public boolean isAdmin(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        String email = usuario.getAttribute("email");
        if (!isAdminEmailAuthorized(email)) {
            return false;
        }
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == PerfilUsuario.ADMIN;
    }

    public boolean isViceDiretora(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        String email = usuario.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == PerfilUsuario.VICE_DIRETORA;
    }

    public boolean isCoordenadora(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        String email = usuario.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == PerfilUsuario.COORDENADORA;
    }

    public boolean isAdminOrViceDiretora(OAuth2User usuario) {
        return isAdmin(usuario) || isViceDiretora(usuario);
    }

    public boolean hasPerfil(OAuth2User oauth2User, PerfilUsuario perfil) {
        if (oauth2User == null || perfil == null) {
            return false;
        }
        String email = oauth2User.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == perfil;
    }

    public boolean hasPerfil(Usuario usuario, PerfilUsuario perfil) {
        return usuario != null && usuario.getPerfil() == perfil;
    }

    public boolean hasPerfilOrAdmin(OAuth2User oauth2User, PerfilUsuario perfil) {
        if (oauth2User == null || perfil == null) {
            return false;
        }
        String email = oauth2User.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        if (u == null) {
            return false;
        }
        return u.getPerfil() == perfil || u.getPerfil() == PerfilUsuario.ADMIN;
    }

    private Set<String> parseEmails(String config) {
        if (config == null || config.isBlank()) {
            return Set.of();
        }
        return Set.of(java.util.Arrays.stream(config.split(","))
                .map(s -> s.trim())
                .map(s -> s.toLowerCase())
                .toArray(String[]::new));
    }
}

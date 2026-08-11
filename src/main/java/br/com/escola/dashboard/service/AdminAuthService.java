package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminAuthService {

    private final Set<String> authorizedEmails;
    private final UsuarioService usuarioService;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;

    private record CacheEntry(Usuario usuario, long timestamp) {
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

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
        Usuario u = findUsuarioCached(email);
        return u != null && u.getPerfil() == PerfilUsuario.ADMIN;
    }

    public boolean isViceDiretora(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        String email = usuario.getAttribute("email");
        Usuario u = findUsuarioCached(email);
        return u != null && u.getPerfil() == PerfilUsuario.VICE_DIRETORA;
    }

    public boolean isCoordenadora(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        String email = usuario.getAttribute("email");
        Usuario u = findUsuarioCached(email);
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
        Usuario u = findUsuarioCached(email);
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
        Usuario u = findUsuarioCached(email);
        if (u == null) {
            return false;
        }
        return u.getPerfil() == perfil || u.getPerfil() == PerfilUsuario.ADMIN;
    }

    private Usuario findUsuarioCached(String email) {
        if (email == null) return null;
        String key = email.trim().toLowerCase();
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.usuario();
        }
        Usuario u = usuarioService.buscarPorEmail(key);
        cache.put(key, new CacheEntry(u, System.currentTimeMillis()));
        return u;
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

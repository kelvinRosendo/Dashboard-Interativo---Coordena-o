package br.com.escola.dashboard.config;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UsuarioService usuarioService;

    public CustomOAuth2UserService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String nome = oauth2User.getAttribute("name");
        String fotoUrl = oauth2User.getAttribute("picture");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email nao encontrado no perfil Google");
        }

        Usuario usuario = usuarioService.buscarOuCriarPorGoogle(googleId, email, nome, fotoUrl);

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())
        );

        return new DefaultOAuth2User(
                authorities,
                Map.of(
                        "sub", googleId != null ? googleId : "",
                        "email", email,
                        "name", nome != null ? nome : "",
                        "picture", fotoUrl != null ? fotoUrl : "",
                        "usuario_id", usuario.getId(),
                        "perfil", usuario.getPerfil().name()
                ),
                "email"
        );
    }
}

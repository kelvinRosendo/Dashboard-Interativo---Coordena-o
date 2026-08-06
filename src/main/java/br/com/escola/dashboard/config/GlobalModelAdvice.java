package br.com.escola.dashboard.config;

import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioService usuarioService;

    public GlobalModelAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void addCommonAttributes(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return;
        }

        String email = principal.getAttribute("email");
        String nome = principal.getAttribute("name");

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);

        if (email != null) {
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario != null) {
                model.addAttribute("perfil", usuario.getPerfil());
            }
        }
    }
}

package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User usuario,
                        HttpServletRequest request,
                        Model model) {
        if (usuario != null) {
            String email = usuario.getAttribute("email");
            Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

            if (usuarioAtual == null) {
                return "redirect:/logout";
            }

            if (usuarioAtual.getStatus() == StatusUsuario.PENDENTE) {
                model.addAttribute("mensagemErro",
                        "Seu acesso ainda nao foi liberado. Aguarde a aprovacao do administrador.");
                model.addAttribute("googleConfigurado", googleConfigurado());
                return "login";
            }

            if (usuarioAtual.getStatus() == StatusUsuario.BLOQUEADO) {
                model.addAttribute("mensagemErro",
                        "Seu acesso foi bloqueado. Contate o administrador.");
                model.addAttribute("googleConfigurado", googleConfigurado());
                return "login";
            }

            return "redirect:/dashboard";
        }

        model.addAttribute("googleConfigurado", googleConfigurado());
        return "login";
    }

    private boolean googleConfigurado() {
        return googleClientId != null
                && !googleClientId.isBlank()
                && !googleClientId.startsWith("configure-");
    }
}

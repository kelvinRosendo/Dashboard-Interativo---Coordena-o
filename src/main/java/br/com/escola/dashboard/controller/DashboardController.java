package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.service.PerfilService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final PerfilService perfilService;
    private final UsuarioService usuarioService;

    public DashboardController(PerfilService perfilService, UsuarioService usuarioService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User oauth2User,
                             RedirectAttributes redirectAttributes) {
        if (oauth2User == null) {
            return "redirect:/login";
        }

        String email = oauth2User.getAttribute("email");
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            usuario = usuarioService.buscarOuCriarPorGoogle(
                    oauth2User.getAttribute("sub") != null ? oauth2User.getAttribute("sub") : null,
                    email,
                    oauth2User.getAttribute("name"),
                    oauth2User.getAttribute("picture") != null ? oauth2User.getAttribute("picture") : null
            );
        }

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getStatus() == StatusUsuario.PENDENTE) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Seu acesso ainda nao foi liberado. Aguarde a aprovacao do administrador.");
            return "redirect:/login";
        }

        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Seu acesso foi bloqueado. Contate o administrador.");
            return "redirect:/login";
        }

        return "redirect:" + perfilService.getDashboardRedirect(usuario);
    }
}

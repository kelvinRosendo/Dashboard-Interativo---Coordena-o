package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.DashboardDTO;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.service.DashboardService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CoordenadoraDashboardController {

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;

    public CoordenadoraDashboardController(DashboardService dashboardService,
                                            UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/coordenadora/dashboard")
    public String painelCoordenadora(@AuthenticationPrincipal OAuth2User oauth2User,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (oauth2User == null) {
            return "redirect:/login";
        }

        String email = oauth2User.getAttribute("email");
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getStatus() != StatusUsuario.ATIVO) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Seu acesso ainda nao foi liberado. Aguarde a aprovacao do administrador.");
            return "redirect:/login";
        }

        DashboardDTO dashboard = dashboardService.coletarDadosCoordenadora(usuario);
        model.addAttribute("dashboard", dashboard);

        return "coordenadora-dashboard";
    }
}

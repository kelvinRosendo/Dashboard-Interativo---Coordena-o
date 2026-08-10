package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.DashboardDTO;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.DashboardService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViceDiretoraController {

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;
    private final AdminAuthService adminAuthService;

    public ViceDiretoraController(DashboardService dashboardService,
                                   UsuarioService usuarioService,
                                   AdminAuthService adminAuthService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping("/vice-diretora")
    public String painelViceDiretora(@AuthenticationPrincipal OAuth2User oauth2User,
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

        if (usuario.getPerfil() != PerfilUsuario.VICE_DIRETORA
                && usuario.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }

        DashboardDTO dashboard = dashboardService.coletarDadosViceDiretora(usuario);
        model.addAttribute("dashboard", dashboard);

        return "vice-diretora";
    }
}

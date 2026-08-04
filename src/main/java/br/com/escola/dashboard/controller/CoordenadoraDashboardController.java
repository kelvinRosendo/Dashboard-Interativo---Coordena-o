package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.service.CardService;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.PerfilService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CoordenadoraDashboardController {

    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final ComunicadoService comunicadoService;

    public CoordenadoraDashboardController(PerfilService perfilService,
                                            UsuarioService usuarioService,
                                            DemandaService demandaService,
                                            SemanaEmFocoService semanaEmFocoService,
                                            ComunicadoService comunicadoService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.comunicadoService = comunicadoService;
    }

    @GetMapping("/coordenadora/dashboard")
    public String painelCoordenadora(@AuthenticationPrincipal OAuth2User oauth2User,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (oauth2User == null) {
            return "redirect:/login";
        }

        String email = oauth2User.getAttribute("email");
        String nome = oauth2User.getAttribute("name");
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Segmento> segmentosDoUsuario = perfilService.getSegmentosDoUsuario(usuario);

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("perfil", usuario.getPerfil());
        model.addAttribute("segmentos", segmentosDoUsuario);
        model.addAttribute("demandaResumo", demandaService.resumoGeral());
        model.addAttribute("comunicados", comunicadoService.listarTodos());
        model.addAttribute("semanaEmFoco", semanaEmFocoService.buscarAtiva().orElse(null));
        model.addAttribute("demandasAtivas", demandaService.listarAtivas());

        return "coordenadora-dashboard";
    }
}

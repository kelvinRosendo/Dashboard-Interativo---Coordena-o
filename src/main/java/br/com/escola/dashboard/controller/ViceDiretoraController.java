package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.service.PerfilService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ViceDiretoraController {

    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final ComunicadoService comunicadoService;
    private final GoogleCalendarService googleCalendarService;

    public ViceDiretoraController(PerfilService perfilService,
                                   UsuarioService usuarioService,
                                   DemandaService demandaService,
                                   SemanaEmFocoService semanaEmFocoService,
                                   ComunicadoService comunicadoService,
                                   GoogleCalendarService googleCalendarService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.comunicadoService = comunicadoService;
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/vice-diretora")
    public String painelViceDiretora(@AuthenticationPrincipal OAuth2User oauth2User,
                                      @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
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

        if (!perfilService.isViceDiretora(usuario) && !perfilService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }

        List<Segmento> todosSegmentos = perfilService.getSegmentosDoUsuario(usuario);

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("perfil", usuario.getPerfil());
        model.addAttribute("segmentos", todosSegmentos);
        model.addAttribute("demandaResumo", demandaService.resumoGeral());
        model.addAttribute("demandasAdmin", demandaService.listarTodasParaAdmin());
        model.addAttribute("statusDemandas", StatusDemanda.values());
        model.addAttribute("comunicados", comunicadoService.listarTodos());
        model.addAttribute("semanaEmFoco", semanaEmFocoService.buscarAtiva().orElse(null));

        try {
            List<GoogleCalendarEventDTO> eventosGoogle = googleCalendarService.listarEventos(
                    googleClient,
                    LocalDate.now(),
                    LocalDate.now().plusDays(14)
            );
            model.addAttribute("eventosGoogle", eventosGoogle);
            model.addAttribute("calendarErro", null);
        } catch (IllegalStateException ex) {
            model.addAttribute("eventosGoogle", List.of());
            model.addAttribute("calendarErro", ex.getMessage());
        }

        return "vice-diretora";
    }
}

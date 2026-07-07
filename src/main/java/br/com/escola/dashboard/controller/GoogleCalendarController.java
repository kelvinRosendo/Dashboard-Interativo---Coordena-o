package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.dto.GoogleCalendarEventRequestDTO;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.AgendaConflictService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.utils.ConflitoModelHelper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final AgendaConflictService agendaConflictService;
    private final AdminAuthService adminAuthService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService,
                                    AgendaConflictService agendaConflictService,
                                    AdminAuthService adminAuthService) {
        this.googleCalendarService = googleCalendarService;
        this.agendaConflictService = agendaConflictService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping("/agenda/eventos/novo")
    public String novoEvento(@AuthenticationPrincipal OAuth2User usuario,
                             @RequestParam(name = "origem", defaultValue = "admin") String origem,
                             @RequestParam(name = "segmento", required = false) String segmento,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        GoogleCalendarEventRequestDTO evento = new GoogleCalendarEventRequestDTO();
        evento.setOrigem(normalizarOrigem(origem));
        evento.setSegmento(segmento);
        evento.setData(LocalDate.now());
        evento.setInicio(LocalTime.of(8, 0));
        evento.setFim(LocalTime.of(9, 0));

        prepararFormulario(model, evento);
        return "novo-evento";
    }

    @PostMapping("/agenda/eventos")
    public String criarEvento(@AuthenticationPrincipal OAuth2User usuario,
                              @Valid @ModelAttribute("evento") GoogleCalendarEventRequestDTO evento,
                              BindingResult bindingResult,
                              @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                              @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        evento.setOrigem(normalizarOrigem(evento.getOrigem()));

        if (bindingResult.hasErrors()) {
            prepararFormulario(model, evento);
            return "novo-evento";
        }

        if (!confirmarConflito) {
            AgendaConflictCheckDTO conflitos = agendaConflictService.buscarConflitos(googleClient, evento.getData(), null);
            if (conflitos.temConflitos()) {
                prepararFormulario(model, evento);
                ConflitoModelHelper.adicionarConflitosAoModelo(model, conflitos);
                return "novo-evento";
            }
        }

        try {
            googleCalendarService.criarEvento(googleClient, evento);
        } catch (IllegalArgumentException ex) {
            String mensagem = ex.getMessage();
            bindingResult.reject("eventoInvalido", mensagem != null ? mensagem : "Evento inválido");
            prepararFormulario(model, evento);
            return "novo-evento";
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
            return "redirect:" + resolverDestino(evento);
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Evento criado no Google Agenda.");
        return "redirect:" + resolverDestino(evento);
    }

    private void prepararFormulario(Model model, GoogleCalendarEventRequestDTO evento) {
        model.addAttribute("evento", evento);
        model.addAttribute("voltarUrl", resolverDestino(evento));
        model.addAttribute("sidebarActive", "coordenadora".equals(evento.getOrigem()) ? "coordenadoras" : "admin");
    }

    private String resolverDestino(GoogleCalendarEventRequestDTO evento) {
        if ("coordenadora".equals(evento.getOrigem())) {
            SegmentoCoordenacao segmento = SegmentoCoordenacao.fromSlug(evento.getSegmento());
            if (segmento != null) {
                return "/coordenadoras/" + segmento.getSlug();
            }
        }

        return "/admin";
    }

    private String normalizarOrigem(String origem) {
        return "coordenadora".equalsIgnoreCase(origem) ? "coordenadora" : "admin";
    }
}

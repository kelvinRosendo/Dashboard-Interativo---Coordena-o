package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    private final GoogleCalendarService googleCalendarService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;

    public AdminController(GoogleCalendarService googleCalendarService,
                           DemandaService demandaService,
                           SemanaEmFocoService semanaEmFocoService) {
        this.googleCalendarService = googleCalendarService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
    }

    @GetMapping("/admin")
    public String painelAdministrativo(@AuthenticationPrincipal OAuth2User usuario,
                                       @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                                       Model model) {
        String nome = usuario != null ? usuario.getAttribute("name") : "Usuario";
        String email = usuario != null ? usuario.getAttribute("email") : null;

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("coordenadoras", Arrays.stream(SegmentoCoordenacao.values())
                .map(segmento -> new CoordenadoraResumo(
                        segmento.getSlug(),
                        segmento.getTitulo(),
                        segmento.getDescricao()
                ))
                .toList());
        model.addAttribute("demandaResumo", demandaService.resumoGeral());
        model.addAttribute("demandasAdmin", demandaService.listarTodasParaAdmin());
        model.addAttribute("statusDemandas", StatusDemanda.values());

        try {
            List<GoogleCalendarEventDTO> eventosGoogle = googleCalendarService.listarEventos(
                    googleClient,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7)
            );
            model.addAttribute("eventosGoogle", eventosGoogle);
            model.addAttribute("calendarErro", null);
        } catch (IllegalStateException ex) {
            model.addAttribute("eventosGoogle", List.of());
            model.addAttribute("calendarErro", ex.getMessage());
        }

        return "admin";
    }

    @GetMapping("/admin/semana-em-foco")
    public String editarSemanaEmFoco(Model model) {
        SemanaEmFoco semana = semanaEmFocoService.buscarAtiva()
                .orElseGet(() -> {
                    SemanaEmFoco nova = new SemanaEmFoco();
                    nova.setAtiva(true);
                    return nova;
                });

        model.addAttribute("semana", semana);
        model.addAttribute("segmentos", SegmentoCoordenacao.values());
        model.addAttribute("prioridades", PrioridadeDemanda.values());
        return "semana-em-foco-form";
    }

    @PostMapping("/admin/semana-em-foco")
    public String salvarSemanaEmFoco(@Valid @ModelAttribute("semana") SemanaEmFoco semana,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("segmentos", SegmentoCoordenacao.values());
            model.addAttribute("prioridades", PrioridadeDemanda.values());
            return "semana-em-foco-form";
        }

        semanaEmFocoService.salvar(semana);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Semana em Foco atualizada com sucesso.");
        return "redirect:/admin";
    }

    public record CoordenadoraResumo(String slug, String nome, String descricao) {
    }
}


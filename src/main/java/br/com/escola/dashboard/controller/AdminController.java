package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    private final GoogleCalendarService googleCalendarService;
    private final DemandaService demandaService;

    public AdminController(GoogleCalendarService googleCalendarService, DemandaService demandaService) {
        this.googleCalendarService = googleCalendarService;
        this.demandaService = demandaService;
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

    public record CoordenadoraResumo(String slug, String nome, String descricao) {
    }
}

package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.escola.dashboard.service.CardService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PathVariable;
@Controller
public class AdminController {

    private final GoogleCalendarService googleCalendarService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final CardService cardService;


    @Value("${app.admin.authorized-emails}")
    private String authorizedEmailsConfig;

    

    public AdminController(GoogleCalendarService googleCalendarService,
        DemandaService demandaService,
        SemanaEmFocoService semanaEmFocoService,
        CardService cardService) {
        this.googleCalendarService = googleCalendarService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.cardService = cardService;
    }

    @GetMapping("/admin")
    public String painelAdministrativo(@AuthenticationPrincipal OAuth2User usuario,
                                       @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        String nome = usuario != null ? usuario.getAttribute("name") : "Usuario";
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Seu e-mail nao esta autorizado para acessar o painel administrativo.");
            return "redirect:/";
        }

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("semanaEmFoco", semanaEmFocoService.buscarAtiva().orElse(null));
        model.addAttribute("coordenadoras", Arrays.stream(SegmentoCoordenacao.values())
                .map(segmento -> new CoordenadoraResumo(
                        segmento.getSlug(),
                        segmento.getTitulo(),
                        segmento.getDescricao()
                ))
                .toList());
        model.addAttribute("demandaResumo", demandaService.resumoGeral());
        model.addAttribute("avisos", cardService.listarPorCategoria(CategoriaCard.AVISO_NOTA));
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

    private boolean isAdminEmailAuthorized(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        Set<String> authorizedEmails = new HashSet<>();
        if (authorizedEmailsConfig != null && !authorizedEmailsConfig.isBlank()) {
            String[] emails = authorizedEmailsConfig.split(",");
            for (String authorizedEmail : emails) {
                authorizedEmails.add(authorizedEmail.trim().toLowerCase());
            }
        }

        return authorizedEmails.contains(email.trim().toLowerCase());
    }

    @GetMapping("/admin/semana-em-foco")
    public String editarSemanaEmFoco(@AuthenticationPrincipal OAuth2User usuario,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Seu e-mail nao esta autorizado para acessar o painel administrativo.");
            return "redirect:/";
        }

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

    @PostMapping("/admin/avisos/{id}/delete")
    public String deletarAviso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cardService.deletarCard(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Aviso excluído com sucesso.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/semana-em-foco")
    public String salvarSemanaEmFoco(@AuthenticationPrincipal OAuth2User usuario,
                                     @Valid @ModelAttribute("semana") SemanaEmFoco semana,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Seu e-mail nao esta autorizado para acessar o painel administrativo.");
            return "redirect:/";
        }

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


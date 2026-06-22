package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.service.RelatorioSemanaEmFocoService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    private final GoogleCalendarService googleCalendarService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final RelatorioSemanaEmFocoService relatorioService;
    private final ComunicadoService comunicadoService;

    @Value("${app.admin.authorized-emails}")
    private String authorizedEmailsConfig;

    public AdminController(GoogleCalendarService googleCalendarService,
                           DemandaService demandaService,
                           SemanaEmFocoService semanaEmFocoService,
                           RelatorioSemanaEmFocoService relatorioService,
                           ComunicadoService comunicadoService) {
        this.googleCalendarService = googleCalendarService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.relatorioService = relatorioService;
        this.comunicadoService = comunicadoService;
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
        model.addAttribute("demandasAdmin", demandaService.listarTodasParaAdmin());
        model.addAttribute("statusDemandas", StatusDemanda.values());
        model.addAttribute("comunicados", comunicadoService.listarTodos());

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

    @PostMapping("/admin/comunicados")
    public String criarComunicado(@AuthenticationPrincipal OAuth2User usuario,
                                  @RequestParam String titulo,
                                  @RequestParam(required = false) String conteudo,
                                  RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        try {
            comunicadoService.criar(titulo, conteudo);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Comunicado publicado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/comunicados/{id}/delete")
    public String excluirComunicado(@AuthenticationPrincipal OAuth2User usuario,
                                    @PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        comunicadoService.excluir(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Comunicado excluido com sucesso.");
        return "redirect:/admin";
    }

    public record CoordenadoraResumo(String slug, String nome, String descricao) {
    }
}


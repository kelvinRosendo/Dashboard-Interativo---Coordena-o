package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.DashboardDTO;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DashboardService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import br.com.escola.dashboard.service.UsuarioService;
import jakarta.validation.Valid;
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

@Controller
public class AdminController {

    private final DashboardService dashboardService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final ComunicadoService comunicadoService;
    private final AdminAuthService adminAuthService;
    private final UsuarioService usuarioService;

    public AdminController(DashboardService dashboardService,
                           SemanaEmFocoService semanaEmFocoService,
                           ComunicadoService comunicadoService,
                           AdminAuthService adminAuthService,
                           UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.comunicadoService = comunicadoService;
        this.adminAuthService = adminAuthService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin")
    public String painelAdministrativo(@AuthenticationPrincipal OAuth2User usuario,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Seu e-mail nao esta autorizado para acessar o painel administrativo.");
            return "redirect:/";
        }

        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null) {
            return "redirect:/login";
        }

        DashboardDTO dashboard = dashboardService.coletarDadosAdmin(usuarioAtual);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("coordenadoras", java.util.Arrays.stream(SegmentoCoordenacao.values())
                .map(segmento -> new CoordenadoraResumo(
                        segmento.getSlug(),
                        segmento.getTitulo(),
                        segmento.getDescricao()
                ))
                .toList());

        return "admin";
    }

    @GetMapping("/admin/semana-em-foco")
    public String editarSemanaEmFoco(@AuthenticationPrincipal OAuth2User usuario,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
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

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
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

    @GetMapping("/admin/comunicados/novo")
    public String exibirFormularioComunicado(@AuthenticationPrincipal OAuth2User usuario,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        return "novo-comunicado";
    }

    @PostMapping("/admin/comunicados/novo")
    public String salvarComunicadoViaFormulario(@AuthenticationPrincipal OAuth2User usuario,
                                                @RequestParam String titulo,
                                                @RequestParam(required = false) String conteudo,
                                                RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
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

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
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

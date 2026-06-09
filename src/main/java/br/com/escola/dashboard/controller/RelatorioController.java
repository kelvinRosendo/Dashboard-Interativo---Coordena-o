package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.RelatorioSemanaEmFocoDTO;
import br.com.escola.dashboard.entity.RelatorioSemanaEmFoco;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.service.RelatorioSemanaEmFocoService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    private final RelatorioSemanaEmFocoService relatorioService;
    private final SemanaEmFocoService semanaEmFocoService;

    @Value("${app.admin.authorized-emails}")
    private String authorizedEmailsConfig;

    public RelatorioController(RelatorioSemanaEmFocoService relatorioService,
                              SemanaEmFocoService semanaEmFocoService) {
        this.relatorioService = relatorioService;
        this.semanaEmFocoService = semanaEmFocoService;
    }

    @GetMapping("/{semanaId}")
    public String obterOuCriar(@PathVariable Long semanaId,
                              @AuthenticationPrincipal OAuth2User usuario,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        String coordenadoraId = usuario.getAttribute("email");
        String coordenadoraNome = usuario.getAttribute("name");

        RelatorioSemanaEmFoco relatorio = relatorioService.criarOuObter(
                semana.get(),
                coordenadoraId,
                coordenadoraNome,
                coordenadoraId
        );

        model.addAttribute("relatorio", relatorio);
        model.addAttribute("semana", semana.get());
        model.addAttribute("usuario", usuario);

        return "relatorio-semana-form";
    }

    @PostMapping("/{semanaId}")
    public String salvarRascunho(@PathVariable Long semanaId,
                                @AuthenticationPrincipal OAuth2User usuario,
                                @Valid @ModelAttribute("relatorio") RelatorioSemanaEmFocoDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String coordenadoraId = usuario.getAttribute("email");

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorioBusca = relatorioService.obterPorSemanaId(semanaId);

        if (relatorioBusca.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        RelatorioSemanaEmFoco relatorio = relatorioBusca.get();

        try {
            relatorioService.atualizar(relatorio.getId(), dto, coordenadoraId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio salvo com sucesso");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Voce nao tem permissao para editar este relatorio");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/" + semanaId;
    }

    @PostMapping("/{semanaId}/finalizar")
    public String finalizar(@PathVariable Long semanaId,
                           @AuthenticationPrincipal OAuth2User usuario,
                           RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String coordenadoraId = usuario.getAttribute("email");
        String coordenadoraNome = usuario.getAttribute("name");

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorioBusca = relatorioService.obterPorSemanaId(semanaId);

        if (relatorioBusca.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        try {
            relatorioService.finalizar(relatorioBusca.get().getId(), coordenadoraId, coordenadoraNome);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio finalizado com sucesso");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Voce nao tem permissao para finalizar este relatorio");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/" + semanaId;
    }

    @GetMapping("/visualizar/{semanaId}")
    public String visualizar(@PathVariable Long semanaId,
                            @AuthenticationPrincipal OAuth2User usuario,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorio = relatorioService.obterPorSemanaId(semanaId);

        if (relatorio.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        model.addAttribute("relatorio", relatorio.get());
        model.addAttribute("semana", semana.get());
        model.addAttribute("usuario", usuario);

        return "relatorio-semana-view";
    }

    @GetMapping("/admin/relatorios")
    public String listarParaAdmin(@AuthenticationPrincipal OAuth2User usuario,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");

        if (!isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/";
        }

        List<RelatorioSemanaEmFoco> relatorios = relatorioService.obterTodos();
        model.addAttribute("relatorios", relatorios);

        return "relatorios-lista-admin";
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
}

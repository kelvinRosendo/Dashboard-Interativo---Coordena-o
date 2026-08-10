package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.dto.DemandaRequestDTO;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.AgendaConflictService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.UsuarioService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DemandaController {

    private final DemandaService demandaService;
    private final AgendaConflictService agendaConflictService;
    private final UsuarioService usuarioService;

    public DemandaController(DemandaService demandaService,
                             AgendaConflictService agendaConflictService,
                             UsuarioService usuarioService) {
        this.demandaService = demandaService;
        this.agendaConflictService = agendaConflictService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/demandas/nova")
    public String novaDemanda(@AuthenticationPrincipal OAuth2User usuario,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (!isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }
        prepararFormulario(model, new DemandaRequestDTO());
        return "nova-demanda";
    }

    @PostMapping("/admin/demandas")
    public String criarDemanda(@AuthenticationPrincipal OAuth2User usuario,
                               @Valid @ModelAttribute("demanda") DemandaRequestDTO demanda,
                               BindingResult bindingResult,
                               @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                               @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            prepararFormulario(model, demanda);
            return "nova-demanda";
        }

        if (demanda.getDataPrazo() != null && !confirmarConflito) {
            AgendaConflictCheckDTO conflitos = agendaConflictService.buscarConflitos(
                    googleClient,
                    demanda.getDataPrazo(),
                    null
            );
            if (conflitos.temConflitos()) {
                prepararFormulario(model, demanda);
                ConflitoModelHelper.adicionarConflitosAoModelo(model, conflitos);
                return "nova-demanda";
            }
        }

        demandaService.criarDemanda(demanda, obterAutor(usuario));
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Demanda cadastrada com sucesso.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/demandas/{id}/status")
    public String atualizarStatusAdmin(@AuthenticationPrincipal OAuth2User usuario,
                                       @PathVariable Long id,
                                       @RequestParam StatusDemanda status,
                                       RedirectAttributes redirectAttributes) {
        if (!isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }

        demandaService.atualizarStatus(id, status);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status da demanda atualizado.");
        return "redirect:/admin";
    }

    @PostMapping("/coordenadoras/{segmento}/demandas/{id}/status")
    public String atualizarStatusCoordenadora(@AuthenticationPrincipal OAuth2User usuario,
                                               @PathVariable String segmento,
                                               @PathVariable Long id,
                                               @RequestParam StatusDemanda status,
                                               RedirectAttributes redirectAttributes) {
        if (!isCoordenadora(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Apenas coordenadoras podem alterar status de demandas.");
            return "redirect:/dashboard";
        }

        SegmentoCoordenacao segmentoEnum = SegmentoCoordenacao.fromSlug(segmento);
        if (segmentoEnum == null) {
            return "redirect:/coordenadoras";
        }

        String email = usuario.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        if (u == null || !usuarioService.buscarSegmentosDoUsuario(u.getId()).stream()
                .anyMatch(s -> s.getSlug().equals(segmento))) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Esta demanda nao pertence aos seus segmentos.");
            return "redirect:/coordenadoras";
        }

        demandaService.atualizarStatusParaSegmento(id, status, segmentoEnum);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status da demanda atualizado.");
        return "redirect:/coordenadoras/" + segmentoEnum.getSlug();
    }

    @PostMapping("/coordenadoras/{segmento}/demandas/visualizar")
    public String visualizarDemandasCoordenadora(@AuthenticationPrincipal OAuth2User usuario,
                                                  @PathVariable String segmento,
                                                  RedirectAttributes redirectAttributes) {
        if (!isCoordenadora(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Apenas coordenadoras podem visualizar demandas.");
            return "redirect:/dashboard";
        }

        String email = usuario.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        if (u == null || !usuarioService.buscarSegmentosDoUsuario(u.getId()).stream()
                .anyMatch(s -> s.getSlug().equals(segmento))) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Este segmento nao esta autorizado para voce.");
            return "redirect:/coordenadoras";
        }

        SegmentoCoordenacao segmentoEnum = SegmentoCoordenacao.fromSlug(segmento);
        if (segmentoEnum == null) {
            return "redirect:/coordenadoras";
        }

        demandaService.marcarNovasPendentesComoVisualizadas(segmentoEnum);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Demandas novas marcadas como vistas.");
        return "redirect:/coordenadoras/" + segmentoEnum.getSlug();
    }

    private void prepararFormulario(Model model, DemandaRequestDTO demanda) {
        model.addAttribute("demanda", demanda);
        model.addAttribute("segmentosDemanda", SegmentoCoordenacao.values());
        model.addAttribute("prioridadesDemanda", PrioridadeDemanda.values());
    }

    private String obterAutor(OAuth2User usuario) {
        if (usuario == null) {
            return "Alissandra";
        }

        String email = usuario.getAttribute("email");
        String nome = usuario.getAttribute("name");
        return email != null ? email : nome;
    }

    private boolean isAdmin(OAuth2User oauth2User) {
        if (oauth2User == null) {
            return false;
        }
        String email = oauth2User.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == PerfilUsuario.ADMIN;
    }

    private boolean isCoordenadora(OAuth2User oauth2User) {
        if (oauth2User == null) {
            return false;
        }
        String email = oauth2User.getAttribute("email");
        Usuario u = usuarioService.buscarPorEmail(email);
        return u != null && u.getPerfil() == PerfilUsuario.COORDENADORA;
    }
}

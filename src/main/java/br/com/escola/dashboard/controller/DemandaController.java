package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.dto.DemandaRequestDTO;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.service.AgendaConflictService;
import br.com.escola.dashboard.service.DemandaService;
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

    public DemandaController(DemandaService demandaService, AgendaConflictService agendaConflictService) {
        this.demandaService = demandaService;
        this.agendaConflictService = agendaConflictService;
    }

    @GetMapping("/admin/demandas/nova")
    public String novaDemanda(Model model) {
        prepararFormulario(model, new DemandaRequestDTO());
        return "nova-demanda";
    }

    @PostMapping("/admin/demandas")
    public String criarDemanda(@Valid @ModelAttribute("demanda") DemandaRequestDTO demanda,
                               BindingResult bindingResult,
                               @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                               @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                               @AuthenticationPrincipal OAuth2User usuario,
                               Model model,
                               RedirectAttributes redirectAttributes) {
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
                adicionarConflitosAoModelo(model, conflitos);
                return "nova-demanda";
            }
        }

        demandaService.criarDemanda(demanda, obterAutor(usuario));
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Demanda cadastrada com sucesso.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/demandas/{id}/status")
    public String atualizarStatusAdmin(@PathVariable Long id,
                                       @RequestParam StatusDemanda status,
                                       RedirectAttributes redirectAttributes) {
        demandaService.atualizarStatus(id, status);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status da demanda atualizado.");
        return "redirect:/admin";
    }

    @PostMapping("/coordenadoras/{segmento}/demandas/{id}/status")
    public String atualizarStatusCoordenadora(@PathVariable String segmento,
                                              @PathVariable Long id,
                                              @RequestParam StatusDemanda status,
                                              RedirectAttributes redirectAttributes) {
        SegmentoCoordenacao segmentoEnum = SegmentoCoordenacao.fromSlug(segmento);
        if (segmentoEnum == null) {
            return "redirect:/coordenadoras";
        }

        demandaService.atualizarStatusParaSegmento(id, status, segmentoEnum);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status da demanda atualizado.");
        return "redirect:/coordenadoras/" + segmentoEnum.getSlug();
    }

    @PostMapping("/coordenadoras/{segmento}/demandas/visualizar")
    public String visualizarDemandasCoordenadora(@PathVariable String segmento,
                                                 RedirectAttributes redirectAttributes) {
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

    private void adicionarConflitosAoModelo(Model model, AgendaConflictCheckDTO conflitos) {
        model.addAttribute("exibirModalConflito", true);
        model.addAttribute("conflitos", conflitos.conflitos());
        model.addAttribute("googleAgendaIndisponivel", conflitos.googleIndisponivel());
        model.addAttribute("avisoGoogle", conflitos.avisoGoogle());
    }

    private String obterAutor(OAuth2User usuario) {
        if (usuario == null) {
            return "Alissandra";
        }

        String email = usuario.getAttribute("email");
        String nome = usuario.getAttribute("name");
        return email != null ? email : nome;
    }
}

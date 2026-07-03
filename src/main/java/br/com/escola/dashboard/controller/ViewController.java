package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.AgendaConflictService;
import br.com.escola.dashboard.service.CardService;
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
public class ViewController {

    private final CardService cardService;
    private final AgendaConflictService agendaConflictService;
    private final AdminAuthService adminAuthService;

    public ViewController(CardService cardService, AgendaConflictService agendaConflictService,
                          AdminAuthService adminAuthService) {
        this.cardService = cardService;
        this.agendaConflictService = agendaConflictService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping("/")
    public String redirecionarParaDashboardTv() {
        return "redirect:/tv/semana";
    }

    @GetMapping("/novo-card")
    public String exibirFormularioNovo(@AuthenticationPrincipal OAuth2User usuario,
                                       @RequestParam(name = "categoria", required = false) CategoriaCard categoria,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        CategoriaCard categoriaSelecionada = categoria != null ? categoria : CategoriaCard.EVENTO;

        CardRequestDTO card = new CardRequestDTO();
        card.setCategoria(categoriaSelecionada);
        card.setStatus(StatusCard.PENDENTE);

        preencherModeloFormulario(model, card, false, null);
        return "novo-card";
    }

    @PostMapping("/salvar-card")
    public String salvarCard(@AuthenticationPrincipal OAuth2User usuario,
                             @Valid @ModelAttribute("card") CardRequestDTO cardRequestDTO,
                             BindingResult bindingResult,
                             @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                             @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            preencherModeloFormulario(model, cardRequestDTO, false, null);
            return "novo-card";
        }

        if (cardRequestDTO.getDataEvento() != null && !confirmarConflito) {
            AgendaConflictCheckDTO conflitos = agendaConflictService.buscarConflitos(
                    googleClient,
                    cardRequestDTO.getDataEvento(),
                    null
            );
            if (conflitos.temConflitos()) {
                preencherModeloFormulario(model, cardRequestDTO, false, null);
                ConflitoModelHelper.adicionarConflitosAoModelo(model, conflitos);
                return "novo-card";
            }
        }

        try {
            cardService.criarCard(cardRequestDTO);
        } catch (IllegalArgumentException ex) {
            String errorMessage = ex.getMessage();
            bindingResult.reject("card.invalido", errorMessage != null ? errorMessage : "Erro ao criar card");
            preencherModeloFormulario(model, cardRequestDTO, false, null);
            return "novo-card";
        }

        return "redirect:/admin";
    }

    @PostMapping("/deletar-card/{id}")
    public String deletarCard(@AuthenticationPrincipal OAuth2User usuario,
                              @PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        cardService.deletarCard(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Card excluido com sucesso.");
        return "redirect:/admin";
    }

    @GetMapping("/editar-card/{id}")
    public String exibirFormularioEdicao(@AuthenticationPrincipal OAuth2User usuario,
                                         @PathVariable Long id,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        CardResponseDTO card = cardService.buscarPorId(id);

        CardRequestDTO requestDTO = new CardRequestDTO();
        requestDTO.setTitulo(card.getTitulo());
        requestDTO.setDescricao(card.getDescricao());
        requestDTO.setCategoria(card.getCategoria());
        requestDTO.setPrioridade(card.getPrioridade());
        requestDTO.setDataEvento(card.getDataEvento());
        requestDTO.setResponsavel(card.getResponsavel());
        requestDTO.setStatus(card.getStatus());
        requestDTO.setObservacoes(card.getObservacoes());

        preencherModeloFormulario(model, requestDTO, true, id);
        return "novo-card";
    }

    @PostMapping("/atualizar-card/{id}")
    public String atualizarCard(@AuthenticationPrincipal OAuth2User usuario,
                                @PathVariable Long id,
                                @Valid @ModelAttribute("card") CardRequestDTO cardRequestDTO,
                                BindingResult bindingResult,
                                @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                                @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (!adminAuthService.isAdmin(usuario)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            preencherModeloFormulario(model, cardRequestDTO, true, id);
            return "novo-card";
        }

        if (cardRequestDTO.getDataEvento() != null && !confirmarConflito) {
            AgendaConflictCheckDTO conflitos = agendaConflictService.buscarConflitos(
                    googleClient,
                    cardRequestDTO.getDataEvento(),
                    id
            );
            if (conflitos.temConflitos()) {
                preencherModeloFormulario(model, cardRequestDTO, true, id);
                ConflitoModelHelper.adicionarConflitosAoModelo(model, conflitos);
                return "novo-card";
            }
        }

        try {
            cardService.atualizarCard(id, cardRequestDTO);
        } catch (IllegalArgumentException ex) {
            String errorMessage = ex.getMessage();
            bindingResult.reject("card.invalido", errorMessage != null ? errorMessage : "Erro ao atualizar card");
            preencherModeloFormulario(model, cardRequestDTO, true, id);
            return "novo-card";
        }

        return "redirect:/admin";
    }

    private void preencherModeloFormulario(Model model, CardRequestDTO card, boolean modoEdicao, Long cardId) {
        CategoriaCard categoriaSelecionada = card.getCategoria() != null ? card.getCategoria() : CategoriaCard.EVENTO;

        model.addAttribute("card", card);
        model.addAttribute("modoEdicao", modoEdicao);
        model.addAttribute("tituloFormulario", categoriaSelecionada.getTituloFormulario());
        model.addAttribute("categoriaSelecionada", categoriaSelecionada);

        if (cardId != null) {
            model.addAttribute("cardId", cardId);
        }
    }

}

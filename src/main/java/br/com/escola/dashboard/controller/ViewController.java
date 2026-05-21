package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.service.AgendaConflictService;
import br.com.escola.dashboard.service.CardService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    private final CardService cardService;
    private final AgendaConflictService agendaConflictService;

    public ViewController(CardService cardService, AgendaConflictService agendaConflictService) {
        this.cardService = cardService;
        this.agendaConflictService = agendaConflictService;
    }

    @GetMapping("/")
    public String redirecionarParaDashboardTv() {
        return "redirect:/tv/semana";
    }

    @GetMapping("/novo-card")
    public String exibirFormularioNovo(@RequestParam(name = "categoria", required = false) CategoriaCard categoria,
                                       Model model) {
        CategoriaCard categoriaSelecionada = categoria != null ? categoria : CategoriaCard.EVENTO;

        CardRequestDTO card = new CardRequestDTO();
        card.setCategoria(categoriaSelecionada);
        card.setStatus(StatusCard.PENDENTE);

        preencherModeloFormulario(model, card, false, null);
        return "novo-card";
    }

    @PostMapping("/salvar-card")
    public String salvarCard(@Valid @ModelAttribute("card") CardRequestDTO cardRequestDTO,
                             BindingResult bindingResult,
                             @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                             @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                             Model model) {
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
                adicionarConflitosAoModelo(model, conflitos);
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

    @GetMapping("/deletar-card/{id}")
    public String deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return "redirect:/admin";
    }

    @GetMapping("/editar-card/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
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
    public String atualizarCard(@PathVariable Long id,
                                @Valid @ModelAttribute("card") CardRequestDTO cardRequestDTO,
                                BindingResult bindingResult,
                                @RequestParam(name = "confirmarConflito", defaultValue = "false") boolean confirmarConflito,
                                @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient googleClient,
                                Model model) {
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
                adicionarConflitosAoModelo(model, conflitos);
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

    private void adicionarConflitosAoModelo(Model model, AgendaConflictCheckDTO conflitos) {
        model.addAttribute("exibirModalConflito", true);
        model.addAttribute("conflitos", conflitos.conflitos());
        model.addAttribute("googleAgendaIndisponivel", conflitos.googleIndisponivel());
        model.addAttribute("avisoGoogle", conflitos.avisoGoogle());
    }

}

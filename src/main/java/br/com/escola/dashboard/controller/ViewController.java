package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController {

    private final CardService cardService;

    public ViewController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/")
    public String paginaInicial(Model model) {
        List<CardResponseDTO> cards = cardService.listarTodos();

        model.addAttribute("cards", cards);
        model.addAttribute("horarios", filtrarPorCategoria(cards, CategoriaCard.HORARIO_PROFESSOR));
        model.addAttribute("faltas", filtrarPorCategoria(cards, CategoriaCard.FALTA_PROFESSOR));
        model.addAttribute("eventos", filtrarPorCategoria(cards, CategoriaCard.EVENTO));
        model.addAttribute("rotinaAdministrativa", filtrarPorCategoria(cards, CategoriaCard.ROTINA_ADMINISTRATIVA));
        model.addAttribute("rotinaAuxiliar", filtrarPorCategoria(cards, CategoriaCard.ROTINA_AUXILIAR));
        model.addAttribute("avisos", filtrarPorCategoria(cards, CategoriaCard.AVISO_NOTA));
        model.addAttribute("pendentes", filtrarPorStatus(cards, StatusCard.PENDENTE));
        model.addAttribute("emAndamento", filtrarPorStatus(cards, StatusCard.EM_ANDAMENTO));
        model.addAttribute("concluidos", filtrarPorStatus(cards, StatusCard.CONCLUIDO));

        return "cards";
    }

    @GetMapping("/novo-card")
    public String exibirFormularioNovo(@RequestParam(name = "categoria", required = false) CategoriaCard categoria,
                                       Model model) {
        CategoriaCard categoriaSelecionada = categoria != null ? categoria : CategoriaCard.EVENTO;

        CardRequestDTO card = new CardRequestDTO();
        card.setCategoria(categoriaSelecionada);
        card.setStatus(StatusCard.PENDENTE);

        model.addAttribute("card", card);
        model.addAttribute("categoriaSelecionada", categoriaSelecionada);
        model.addAttribute("tituloFormulario", categoriaSelecionada.getTituloFormulario());
        model.addAttribute("modoEdicao", false);

        return "novo-card";
    }

    @PostMapping("/salvar-card")
    public String salvarCard(@ModelAttribute("card") CardRequestDTO cardRequestDTO) {
        cardService.criarCard(cardRequestDTO);
        return "redirect:/";
    }

    @GetMapping("/deletar-card/{id}")
    public String deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return "redirect:/";
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

        model.addAttribute("card", requestDTO);
        model.addAttribute("cardId", id);
        model.addAttribute("categoriaSelecionada", card.getCategoria());
        model.addAttribute("tituloFormulario", card.getCategoria().getTituloFormulario());
        model.addAttribute("modoEdicao", true);

        return "novo-card";
    }

    @PostMapping("/atualizar-card/{id}")
    public String atualizarCard(@PathVariable Long id,
                                @ModelAttribute("card") CardRequestDTO cardRequestDTO) {
        cardService.atualizarCard(id, cardRequestDTO);
        return "redirect:/";
    }

    private List<CardResponseDTO> filtrarPorCategoria(List<CardResponseDTO> cards, CategoriaCard categoria) {
        return cards.stream()
                .filter(card -> card.getCategoria() == categoria)
                .toList();
    }

    private List<CardResponseDTO> filtrarPorStatus(List<CardResponseDTO> cards, StatusCard status) {
        return cards.stream()
                .filter(card -> card.getStatus() == status)
                .toList();
    }
}

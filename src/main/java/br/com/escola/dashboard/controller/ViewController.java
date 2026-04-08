package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🔹 ViewController
 *
 * Esse controller é responsável por retornar páginas HTML (Thymeleaf),
 * diferente dos controllers REST que retornam JSON.
 */
@Controller
public class ViewController {

    private final CardService cardService;

    /**
     * 🔹 Injeção do CardService
     */
    public ViewController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 🔹 Página inicial
     *
     * Lista todos os cards cadastrados e envia para a página cards.html
     */
    @GetMapping("/")
    public String paginaInicial(Model model) {
        List<CardResponseDTO> cards = cardService.listarTodos();
        List<CardResponseDTO> pendentes = cards.stream()
                .filter(card -> card.getStatus() == StatusCard.PENDENTE)
                .toList();
        List<CardResponseDTO> emAndamento = cards.stream()
                .filter(card -> card.getStatus() == StatusCard.EM_ANDAMENTO)
                .toList();
        List<CardResponseDTO> concluidos = cards.stream()
                .filter(card -> card.getStatus() == StatusCard.CONCLUIDO)
                .toList();

        model.addAttribute("cards", cards);
        model.addAttribute("pendentes", pendentes);
        model.addAttribute("emAndamento", emAndamento);
        model.addAttribute("concluidos", concluidos);
        return "cards";
    }

    /**
     * 🔹 Abre o formulário para criar um novo card
     */
    @GetMapping("/novo-card")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("card", new CardRequestDTO());
        model.addAttribute("modoEdicao", false);
        return "novo-card";
    }

    /**
     * 🔹 Salva um novo card
     */
    @PostMapping("/salvar-card")
    public String salvarCard(@ModelAttribute("card") CardRequestDTO cardRequestDTO) {
        cardService.criarCard(cardRequestDTO);
        return "redirect:/";
    }

    /**
     * 🔹 Deleta um card pelo ID
     */
    @GetMapping("/deletar-card/{id}")
    public String deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return "redirect:/";
    }

    /**
     * 🔹 Abre o formulário de edição
     *
     * Busca o card pelo ID, preenche os campos e envia para a mesma página do formulário.
     */
    @GetMapping("/editar-card/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        CardResponseDTO card = cardService.buscarPorId(id);

        CardRequestDTO requestDTO = new CardRequestDTO();
        requestDTO.setTitulo(card.getTitulo());
        requestDTO.setDescricao(card.getDescricao());
        requestDTO.setCategoria(card.getCategoria());
        requestDTO.setPrioridade(card.getPrioridade());

        model.addAttribute("card", requestDTO);
        model.addAttribute("cardId", id);
        model.addAttribute("modoEdicao", true);

        return "novo-card";
    }

    /**
     * 🔹 Atualiza um card existente
     */
    @PostMapping("/atualizar-card/{id}")
    public String atualizarCard(@PathVariable Long id,
                                @ModelAttribute("card") CardRequestDTO cardRequestDTO) {
        cardService.atualizarCard(id, cardRequestDTO);
        return "redirect:/";
    }
}

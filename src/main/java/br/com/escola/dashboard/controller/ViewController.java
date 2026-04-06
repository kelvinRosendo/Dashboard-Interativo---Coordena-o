package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 🔹 ViewController
 *
 * Esse controller é responsável por retornar páginas HTML (Thymeleaf),
 * diferente dos outros controllers que retornam JSON.
 */
@Controller
public class ViewController {

    private final CardService cardService;

    /**
     * 🔹 Injeção de dependência do CardService
     */
    public ViewController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 🔹 Página inicial (lista de cards)
     *
     * Quando acessar: http://localhost:8080/
     *
     * O método:
     * - busca todos os cards no banco
     * - envia para o HTML (cards.html)
     */
    @GetMapping("/")
    public String paginaInicial(Model model) {
        model.addAttribute("cards", cardService.listarTodos());
        return "cards"; // arquivo: templates/cards.html
    }

    /**
     * 🔹 Exibe o formulário para criar um novo card
     *
     * Quando acessar: http://localhost:8080/novo-card
     *
     * O método:
     * - cria um objeto vazio (CardRequestDTO)
     * - envia para o HTML (novo-card.html)
     */
    @GetMapping("/novo-card")
    public String exibirFormulario(Model model) {
        model.addAttribute("card", new CardRequestDTO());
        return "novo-card"; // arquivo: templates/novo-card.html
    }

    /**
     * 🔹 Recebe os dados do formulário e salva no banco
     *
     * Quando o formulário for enviado (POST):
     * /salvar-card
     *
     * O método:
     * - recebe os dados preenchidos no formulário
     * - chama o service para salvar
     * - redireciona para a página inicial
     */
    @PostMapping("/salvar-card")
    public String salvarCard(@ModelAttribute("card") CardRequestDTO cardRequestDTO) {
        cardService.criarCard(cardRequestDTO);
        return "redirect:/"; // volta pra lista de cards
    }

    /**
     * 🔹 Deleta um card pelo ID
     *
     * Quando acessar: /deletar-card/{id}
     *
     * O método:
     * - chama o service para deletar
     * - redireciona para a página inicial
     */
    @GetMapping("/deletar-card/{id}")
    public String deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return "redirect:/";
    }
}
package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final CardService cardService;

    public ViewController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/")
    public String paginaInicial(Model model) {
        model.addAttribute("cards", cardService.listarTodos());
        return "cards";
    }
}
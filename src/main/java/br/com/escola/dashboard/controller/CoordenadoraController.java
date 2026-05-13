package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Locale;

@Controller
public class CoordenadoraController {

    private static final List<SegmentoCoordenadora> SEGMENTOS = List.of(
            new SegmentoCoordenadora("infantil", "Educacao Infantil", "Demandas de rotina e acompanhamento do Infantil."),
            new SegmentoCoordenadora("fundamental-1", "Fundamental 1", "Demandas de rotina e acompanhamento do Fundamental 1."),
            new SegmentoCoordenadora("fundamental-2", "Fundamental 2", "Demandas de rotina e acompanhamento do Fundamental 2."),
            new SegmentoCoordenadora("ensino-medio", "Ensino Medio", "Demandas de rotina e acompanhamento do Ensino Medio.")
    );

    private final CardService cardService;

    public CoordenadoraController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/coordenadoras")
    public String listarCoordenadoras(Model model) {
        model.addAttribute("segmentos", SEGMENTOS);
        return "coordenadoras";
    }

    @GetMapping("/coordenadoras/{slug}")
    public String painelCoordenadora(@PathVariable String slug, Model model) {
        SegmentoCoordenadora segmento = SEGMENTOS.stream()
                .filter(item -> item.slug().equals(slug))
                .findFirst()
                .orElse(null);

        if (segmento == null) {
            return "redirect:/coordenadoras";
        }

        List<CardResponseDTO> semanas = cardService.listarPorCategoria(CategoriaCard.SEMANA_EM_FOCO).stream()
                .filter(card -> contemSegmento(card, segmento.nome()))
                .toList();

        model.addAttribute("segmento", segmento);
        model.addAttribute("semanas", semanas);
        model.addAttribute("tarefas", cardService.listarPorCategoria(CategoriaCard.ROTINA_COORDENADORES));
        model.addAttribute("avisos", cardService.listarPorCategoria(CategoriaCard.AVISO_NOTA));
        model.addAttribute("faltas", cardService.listarPorCategoria(CategoriaCard.FALTA_PROFESSOR));

        return "coordenadora";
    }

    private boolean contemSegmento(CardResponseDTO card, String segmento) {
        String alvo = segmento.toLowerCase(Locale.ROOT);
        return contem(card.getTitulo(), alvo)
                || contem(card.getDescricao(), alvo)
                || contem(card.getResponsavel(), alvo);
    }

    private boolean contem(String texto, String alvo) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(alvo);
    }

    public record SegmentoCoordenadora(String slug, String nome, String descricao) {
    }
}

package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.service.CardService;
import br.com.escola.dashboard.service.DemandaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Controller
public class CoordenadoraController {

    private static final List<SegmentoCoordenadora> SEGMENTOS = Arrays.stream(SegmentoCoordenacao.values())
            .map(segmento -> new SegmentoCoordenadora(
                    segmento.getSlug(),
                    segmento.getTitulo(),
                    segmento.getDescricao()
            ))
            .toList();

    private final CardService cardService;
    private final DemandaService demandaService;

    public CoordenadoraController(CardService cardService, DemandaService demandaService) {
        this.cardService = cardService;
        this.demandaService = demandaService;
    }

    @GetMapping("/coordenadoras")
    public String listarCoordenadoras(Model model) {
        model.addAttribute("segmentos", SEGMENTOS);
        return "coordenadoras";
    }

    @GetMapping("/coordenadoras/{slug}")
    public String painelCoordenadora(@PathVariable String slug, Model model) {
        SegmentoCoordenacao segmentoEnum = SegmentoCoordenacao.fromSlug(slug);

        if (segmentoEnum == null) {
            return "redirect:/coordenadoras";
        }

        SegmentoCoordenadora segmento = new SegmentoCoordenadora(
                segmentoEnum.getSlug(),
                segmentoEnum.getTitulo(),
                segmentoEnum.getDescricao()
        );

        List<CardResponseDTO> semanas = cardService.listarPorCategoria(CategoriaCard.SEMANA_EM_FOCO).stream()
                .filter(card -> contemSegmento(card, segmento.nome()))
                .toList();

        model.addAttribute("segmento", segmento);
        model.addAttribute("segmentoEnum", segmentoEnum);
        model.addAttribute("semanas", semanas);
        model.addAttribute("tarefas", cardService.listarPorCategoria(CategoriaCard.ROTINA_COORDENADORES));
        model.addAttribute("avisos", cardService.listarPorCategoria(CategoriaCard.AVISO_NOTA));
        model.addAttribute("faltas", cardService.listarPorCategoria(CategoriaCard.FALTA_PROFESSOR));
        model.addAttribute("demandas", demandaService.listarPorSegmento(segmentoEnum));
        model.addAttribute("demandaProgresso", demandaService.calcularProgressoPorSegmento(segmentoEnum));
        model.addAttribute("demandasPendentes", demandaService.contarPendentesPorSegmento(segmentoEnum));

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

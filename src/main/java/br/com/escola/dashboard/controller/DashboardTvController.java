package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CalendarioDiaDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.service.CardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/tv")
public class DashboardTvController {

    private static final DateTimeFormatter TITULO_MES = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", new Locale("pt", "BR"));
    private static final DateTimeFormatter TITULO_SEMANA = DateTimeFormatter.ofPattern("dd/MM", new Locale("pt", "BR"));

    private final CardService cardService;

    public DashboardTvController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping({"", "/semana"})
    public String semanaEmFoco(Model model) {
        List<CardResponseDTO> cards = cardService.listarTodos();

        List<CardResponseDTO> semanas = filtrarPorCategoria(cards, CategoriaCard.SEMANA_EM_FOCO);
        CardResponseDTO semanaAtual = semanas.stream()
                .filter(card -> card.getStatus() != StatusCard.CONCLUIDO)
                .sorted(comparadorPainel())
                .findFirst()
                .orElse(semanas.stream().sorted(comparadorPainel()).findFirst().orElse(null));

        List<CardResponseDTO> manutencao = Stream.concat(
                        filtrarPorCategoria(cards, CategoriaCard.ROTINA_ADMINISTRATIVA).stream(),
                        filtrarPorCategoria(cards, CategoriaCard.ROTINA_COORDENADORES).stream())
                .filter(card -> card.getStatus() != StatusCard.CONCLUIDO)
                .sorted(comparadorPainel())
                .limit(6)
                .toList();

        model.addAttribute("semanaAtual", semanaAtual);
        model.addAttribute("semanas", semanas);
        model.addAttribute("manutencao", manutencao);
        model.addAttribute("avisos", limitar(filtrarPorCategoria(cards, CategoriaCard.AVISO_NOTA), 4));
        model.addAttribute("faltas", limitar(filtrarPorCategoria(cards, CategoriaCard.FALTA_PROFESSOR), 5));
        model.addAttribute("substituicoes", limitar(filtrarPorCategoria(cards, CategoriaCard.SUBSTITUICAO), 5));
        model.addAttribute("hoje", LocalDate.now());

        return "dashboard-semana";
    }

    @GetMapping("/calendario")
    public String calendario(@RequestParam(name = "modo", defaultValue = "mensal") String modo,
                             @RequestParam(name = "referencia", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referencia,
                             Model model) {
        LocalDate dataReferencia = referencia != null ? referencia : LocalDate.now();
        boolean modoSemanal = "semanal".equalsIgnoreCase(modo);

        LocalDate inicio;
        LocalDate fim;
        String tituloPeriodo;

        if (modoSemanal) {
            inicio = dataReferencia.with(DayOfWeek.MONDAY);
            fim = inicio.plusDays(6);
            tituloPeriodo = "Semana de " + inicio.format(TITULO_SEMANA) + " a " + fim.format(TITULO_SEMANA);
        } else {
            LocalDate primeiroDiaMes = dataReferencia.withDayOfMonth(1);
            LocalDate ultimoDiaMes = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());
            inicio = primeiroDiaMes.minusDays(primeiroDiaMes.getDayOfWeek().getValue() - 1L);
            fim = ultimoDiaMes.plusDays(7L - ultimoDiaMes.getDayOfWeek().getValue());
            tituloPeriodo = dataReferencia.format(TITULO_MES);
        }

        List<CardResponseDTO> cardsComData = cardService.listarTodos().stream()
                .filter(card -> card.getDataEvento() != null)
                .filter(card -> !card.getDataEvento().isBefore(inicio) && !card.getDataEvento().isAfter(fim))
                .sorted(comparadorPainel())
                .toList();

        Map<LocalDate, List<CardResponseDTO>> itensPorData = cardsComData.stream()
                .collect(Collectors.groupingBy(CardResponseDTO::getDataEvento));

        List<CalendarioDiaDTO> dias = inicio.datesUntil(fim.plusDays(1))
                .map(data -> new CalendarioDiaDTO(
                        data,
                        data.equals(LocalDate.now()),
                        !modoSemanal && data.getMonth() != dataReferencia.getMonth(),
                        itensPorData.getOrDefault(data, List.of())))
                .toList();

        model.addAttribute("modo", modoSemanal ? "semanal" : "mensal");
        model.addAttribute("modoSemanal", modoSemanal);
        model.addAttribute("tituloPeriodo", tituloPeriodo);
        model.addAttribute("dias", dias);
        model.addAttribute("eventosHoje", itensPorData.getOrDefault(LocalDate.now(), List.of()));

        return "dashboard-calendario";
    }

    private List<CardResponseDTO> filtrarPorCategoria(List<CardResponseDTO> cards, CategoriaCard categoria) {
        return cards.stream()
                .filter(card -> card.getCategoria() == categoria)
                .toList();
    }

    private List<CardResponseDTO> limitar(List<CardResponseDTO> cards, int limite) {
        return cards.stream()
                .filter(card -> card.getStatus() != StatusCard.CONCLUIDO)
                .sorted(comparadorPainel())
                .limit(limite)
                .toList();
    }

    private Comparator<CardResponseDTO> comparadorPainel() {
        return Comparator
                .comparing(CardResponseDTO::getDataEvento, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(CardResponseDTO::getDataCriacao, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}

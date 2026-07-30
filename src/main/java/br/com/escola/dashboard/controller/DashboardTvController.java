package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CalendarioDiaDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.comunicado;
import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.service.CardService;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.GoogleCalendarService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/tv")
public class DashboardTvController {

    private static final DateTimeFormatter TITULO_MES = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
    private static final DateTimeFormatter TITULO_SEMANA = DateTimeFormatter.ofPattern("dd/MM", Locale.forLanguageTag("pt-BR"));

    private final CardService cardService;
    private final GoogleCalendarService googleCalendarService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final DemandaService demandaService;
    private final ComunicadoService comunicadoService;

    public DashboardTvController(CardService cardService,
                                 GoogleCalendarService googleCalendarService,
                                 OAuth2AuthorizedClientService authorizedClientService,
                                 SemanaEmFocoService semanaEmFocoService,
                                 DemandaService demandaService,
                                 ComunicadoService comunicadoService) {
        this.cardService = cardService;
        this.googleCalendarService = googleCalendarService;
        this.authorizedClientService = authorizedClientService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.demandaService = demandaService;
        this.comunicadoService = comunicadoService;
    }

    @GetMapping({"", "/semana"})
    public String semanaEmFoco(@RequestParam(name = "modo", defaultValue = "padrao") String modo,
                               @RequestParam(name = "timer", defaultValue = "30") Integer timer,
                               Model model) {
        boolean modoDashboard = "dashboard".equalsIgnoreCase(modo);
        int timerSegundos = normalizarTimer(timer);

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
                .limit(modoDashboard ? 4 : 6)
                .toList();

        Optional<SemanaEmFoco> semanaEmFocoOpt = semanaEmFocoService.buscarAtiva();
        if (semanaEmFocoOpt.isPresent()) {
            SemanaEmFoco semanaEmFoco = semanaEmFocoOpt.get();
            model.addAttribute("semanaEmFoco", semanaEmFoco);
            List<Demanda> demandasSemana = demandaService.listarAtivasPorSegmento(semanaEmFoco.getSegmento());
            model.addAttribute("demandasSemana", demandasSemana);
        } else {
            model.addAttribute("semanaEmFoco", null);
            model.addAttribute("demandasSemana", List.of());
        }

        model.addAttribute("semanaAtual", semanaAtual);
        model.addAttribute("segmentoSemana", resolverSegmentoSemana(semanaAtual));
        model.addAttribute("semanas", semanas);
        model.addAttribute("manutencao", manutencao);
        List<comunicado> comunicados = comunicadoService.listarTodos();
        int limiteComunicados = modoDashboard ? 3 : 4;
        List<comunicado> comunicadosLimitados = comunicados.stream().limit(limiteComunicados).toList();
        model.addAttribute("comunicados", comunicadosLimitados);
        model.addAttribute("faltas", limitar(filtrarPorCategoria(cards, CategoriaCard.FALTA_PROFESSOR), modoDashboard ? 3 : 5));
        model.addAttribute("substituicoes", limitar(filtrarPorCategoria(cards, CategoriaCard.SUBSTITUICAO), modoDashboard ? 3 : 5));
        model.addAttribute("hoje", LocalDate.now());
        model.addAttribute("modoDashboard", modoDashboard);
        model.addAttribute("timerSegundos", timerSegundos);

        return "dashboard-semana";
    }


    @GetMapping("/calendario")
    public String calendario(@RequestParam(name = "modo", defaultValue = "mensal") String modo,
                             @RequestParam(name = "timer", defaultValue = "30") Integer timer,
                             @RequestParam(name = "referencia", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referencia,
                             Authentication authentication,
                             Model model) {
        LocalDate dataReferencia = referencia != null ? referencia : LocalDate.now();
        boolean modoDashboard = "dashboard".equalsIgnoreCase(modo);
        boolean modoSemanal = !modoDashboard && "semanal".equalsIgnoreCase(modo);
        int timerSegundos = normalizarTimer(timer);

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
            tituloPeriodo = capitalizarPrimeiraLetra(dataReferencia.format(TITULO_MES));
        }

        List<CardResponseDTO> cardsComData = cardService.listarTodos().stream()
                .filter(card -> card.getDataEvento() != null)
                .filter(card -> !card.getDataEvento().isBefore(inicio) && !card.getDataEvento().isAfter(fim))
                .sorted(comparadorPainel())
                .toList();

        Map<LocalDate, List<CardResponseDTO>> itensPorData = cardsComData.stream()
                .collect(Collectors.groupingBy(card -> card.getDataEvento()));

        List<GoogleCalendarEventDTO> eventosGoogle = List.of();
        String calendarErro = null;
        try {
            OAuth2AuthorizedClient googleClient = resolverGoogleClient(authentication);
            eventosGoogle = googleCalendarService.listarEventos(googleClient, inicio, fim);
        } catch (IllegalStateException ex) {
            calendarErro = ex.getMessage();
        }

        Map<LocalDate, List<GoogleCalendarEventDTO>> googlePorData = eventosGoogle.stream()
                .collect(Collectors.groupingBy(evento -> evento.getData()));

        List<CalendarioDiaDTO> dias = inicio.datesUntil(fim.plusDays(1))
                .map(data -> new CalendarioDiaDTO(
                        data,
                        data.equals(LocalDate.now()),
                        !modoSemanal && data.getMonth() != dataReferencia.getMonth(),
                        itensPorData.getOrDefault(data, List.of()),
                        googlePorData.getOrDefault(data, List.of())))
                .toList();

        model.addAttribute("modo", modoSemanal ? "semanal" : "mensal");
        model.addAttribute("modoSemanal", modoSemanal);
        model.addAttribute("modoDashboard", modoDashboard);
        model.addAttribute("timerSegundos", timerSegundos);
        model.addAttribute("tituloPeriodo", tituloPeriodo);
        model.addAttribute("dias", dias);
        model.addAttribute("eventosHoje", itensPorData.getOrDefault(LocalDate.now(), List.of()));
        model.addAttribute("eventosGoogleHoje", googlePorData.getOrDefault(LocalDate.now(), List.of()));
        model.addAttribute("calendarErro", calendarErro);

        // Dados semanais para a lateral do dashboard
        if (modoDashboard) {
            LocalDate inicioSemana = LocalDate.now().with(DayOfWeek.MONDAY);
            LocalDate fimSemana = inicioSemana.plusDays(6);

            List<CardResponseDTO> eventosSemana = cardService.listarTodos().stream()
                    .filter(card -> card.getDataEvento() != null)
                    .filter(card -> !card.getDataEvento().isBefore(inicioSemana) && !card.getDataEvento().isAfter(fimSemana))
                    .sorted(comparadorPainel())
                    .toList();

            List<GoogleCalendarEventDTO> eventosGoogleSemana = eventosGoogle.stream()
                    .filter(evento -> !evento.getData().isBefore(inicioSemana) && !evento.getData().isAfter(fimSemana))
                    .toList();

            List<Demanda> demandasSemana = demandaService.listarAtivas().stream()
                    .filter(demanda -> demanda.getDataPrazo() != null)
                    .filter(demanda -> !demanda.getDataPrazo().isBefore(inicioSemana) && !demanda.getDataPrazo().isAfter(fimSemana))
                    .sorted(Comparator.comparing(demanda -> demanda.getDataPrazo()))
                    .toList();

            model.addAttribute("eventosSemana", eventosSemana);
            model.addAttribute("eventosGoogleSemana", eventosGoogleSemana);
            model.addAttribute("demandasSemana", demandasSemana);
        }

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

    private String resolverSegmentoSemana(CardResponseDTO semanaAtual) {
        if (semanaAtual == null) {
            return "Semana nao definida";
        }

        if (temTexto(semanaAtual.getResponsavel()) && semanaAtual.getResponsavel().trim().length() > 2) {
            return semanaAtual.getResponsavel().trim();
        }

        if (temTexto(semanaAtual.getTitulo())) {
            String titulo = semanaAtual.getTitulo().trim();
            String tituloNormalizado = titulo.toLowerCase(Locale.ROOT);

            if (tituloNormalizado.endsWith(" em foco")) {
                return titulo.substring(0, titulo.length() - " em foco".length()).trim();
            }

            return titulo;
        }

        return "Semana em foco";
    }

    private String capitalizarPrimeiraLetra(String texto) {
        if (!temTexto(texto)) {
            return texto;
        }

        return texto.substring(0, 1).toUpperCase(new Locale("pt", "BR")) + texto.substring(1);
    }

    private boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    private OAuth2AuthorizedClient resolverGoogleClient(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            return null;
        }

        if (!"google".equals(oauthToken.getAuthorizedClientRegistrationId())) {
            return null;
        }

        return authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
    }

    private int normalizarTimer(Integer timer) {
        if (timer == null) {
            return 30;
        }

        return Math.max(5, Math.min(timer, 3600));
    }

    private Comparator<CardResponseDTO> comparadorPainel() {
        return Comparator
                .<CardResponseDTO, LocalDate>comparing(card -> card.getDataEvento(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(CardResponseDTO::getDataCriacao, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}

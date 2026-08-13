package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.dto.AgendaConflictDTO;
import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.repository.CardRepository;
import br.com.escola.dashboard.repository.DemandaRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AgendaConflictService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String ORIGEM_SISTEMA = "Sistema";
    private static final String ORIGEM_GOOGLE = "Google Agenda";

    private final CardRepository cardRepository;
    private final DemandaRepository demandaRepository;
    private final GoogleCalendarService googleCalendarService;

    public AgendaConflictService(CardRepository cardRepository,
                                 DemandaRepository demandaRepository,
                                 GoogleCalendarService googleCalendarService) {
        this.cardRepository = cardRepository;
        this.demandaRepository = demandaRepository;
        this.googleCalendarService = googleCalendarService;
    }

    public AgendaConflictCheckDTO buscarConflitos(OAuth2AuthorizedClient googleClient,
                                                  LocalDate data,
                                                  Long ignorarCardId) {
        if (data == null) {
            return new AgendaConflictCheckDTO(List.of(), false, false, null);
        }

        List<AgendaConflictDTO> conflitos = new ArrayList<>();
        conflitos.addAll(buscarConflitosInternos(data, ignorarCardId));
        AgendaConflictCheckDTO googleCheck = buscarConflitosGoogle(googleClient, data);
        conflitos.addAll(googleCheck.conflitos());

        conflitos.sort(Comparator.<AgendaConflictDTO, String>comparing(c -> c.horario()).thenComparing(c -> c.titulo()));

        return new AgendaConflictCheckDTO(
                conflitos,
                googleCheck.googleConsultado(),
                googleCheck.googleIndisponivel(),
                googleCheck.avisoGoogle()
        );
    }

    private List<AgendaConflictDTO> buscarConflitosInternos(LocalDate data, Long ignorarCardId) {
        List<AgendaConflictDTO> conflitos = new ArrayList<>();
        conflitos.addAll(buscarConflitosCards(data, ignorarCardId));
        conflitos.addAll(buscarConflitosDemandas(data));
        return conflitos;
    }

    private List<AgendaConflictDTO> buscarConflitosCards(LocalDate data, Long ignorarCardId) {
        return cardRepository.findByDataEventoOrderByTituloAsc(data).stream()
                .filter(card -> ignorarCardId == null || !ignorarCardId.equals(card.getId()))
                .map(card -> new AgendaConflictDTO(
                        card.getTitulo(),
                        formatarHorarioInterno(card),
                        ORIGEM_SISTEMA
                ))
                .toList();
    }

    private List<AgendaConflictDTO> buscarConflitosDemandas(LocalDate data) {
        return demandaRepository.findByDataPrazoOrderByTituloAsc(data).stream()
                .map(demanda -> new AgendaConflictDTO(
                        demanda.getTitulo(),
                        "Prazo de demanda",
                        ORIGEM_SISTEMA
                ))
                .toList();
    }

    private AgendaConflictCheckDTO buscarConflitosGoogle(OAuth2AuthorizedClient googleClient, LocalDate data) {
        if (!googleCalendarService.podeConsultar(googleClient)) {
            return new AgendaConflictCheckDTO(
                    List.of(),
                    false,
                    true,
                    "Google Agenda nao autorizado. A verificacao considerou apenas eventos do sistema."
            );
        }

        try {
            List<AgendaConflictDTO> conflitos = googleCalendarService.listarEventos(googleClient, data, data).stream()
                    .map(evento -> new AgendaConflictDTO(
                            evento.getTitulo(),
                            formatarHorarioGoogle(evento),
                            ORIGEM_GOOGLE
                    ))
                    .toList();

            return new AgendaConflictCheckDTO(conflitos, true, false, null);
        } catch (IllegalStateException ex) {
            return new AgendaConflictCheckDTO(
                    List.of(),
                    false,
                    true,
                    "Nao foi possivel consultar o Google Agenda. A verificacao considerou apenas eventos do sistema."
            );
        }
    }

    private String formatarHorarioInterno(Card card) {
        if (card.getDataEvento() != null) {
            return card.getDataEvento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "Sem horario definido";
    }

    private String formatarHorarioGoogle(GoogleCalendarEventDTO evento) {
        if (evento.isDiaInteiro()) {
            return "Dia inteiro";
        }

        if (evento.getInicio() == null) {
            return "Horario nao informado";
        }

        String inicio = evento.getInicio().toLocalTime().format(HORA);
        if (evento.getFim() == null) {
            return inicio;
        }

        return inicio + " - " + evento.getFim().toLocalTime().format(HORA);
    }
}

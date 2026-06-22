package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.repository.CardRepository;
import br.com.escola.dashboard.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgendaConflictServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @InjectMocks
    private AgendaConflictService agendaConflictService;


    @Test
    void buscarConflitos_deveCombinarEventosInternosEGoogle() {
        LocalDate data = LocalDate.of(2026, 5, 20);
        Card card = mock(Card.class);
        when(card.getTitulo()).thenReturn("Reuniao interna");

        OAuth2AuthorizedClient googleClient = mock(OAuth2AuthorizedClient.class);
        GoogleCalendarEventDTO eventoGoogle = new GoogleCalendarEventDTO(
                "Evento Google",
                null,
                null,
                data,
                data.atTime(9, 0),
                data.atTime(10, 0),
                false
        );

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(googleClient)).thenReturn(true);
        when(googleCalendarService.listarEventos(googleClient, data, data)).thenReturn(List.of(eventoGoogle));

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(googleClient, data, null);

        assertEquals(2, resultado.conflitos().size());
        assertTrue(resultado.conflitos().stream().anyMatch(item -> "Sistema".equals(item.origem())));
        assertTrue(resultado.conflitos().stream().anyMatch(item -> "Google Agenda".equals(item.origem())));
        assertFalse(resultado.googleIndisponivel());
    }

    @Test
    void buscarConflitos_semGoogle_deveRetornarApenasInternos() {
        LocalDate data = LocalDate.of(2026, 5, 21);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of());
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertTrue(resultado.conflitos().isEmpty());
        assertTrue(resultado.googleIndisponivel());
        verify(googleCalendarService).podeConsultar(null);
    }

    @Test
    void buscarConflitos_deveIgnorarCardEmEdicao() {
        LocalDate data = LocalDate.of(2026, 5, 22);
        Card card = mock(Card.class);
        when(card.getId()).thenReturn(8L);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(any())).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, 8L);

        assertTrue(resultado.conflitos().isEmpty());
    }

    @Test
    void buscarConflitos_deveIncluirDemandasComPrazoNoDia() {
        LocalDate data = LocalDate.of(2026, 5, 23);
        Demanda demanda = mock(Demanda.class);
        when(demanda.getTitulo()).thenReturn("Enviar relatorio semanal");

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of());
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of(demanda));
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(1, resultado.conflitos().size());
        assertEquals("Enviar relatorio semanal", resultado.conflitos().get(0).titulo());
        assertEquals("Sistema", resultado.conflitos().get(0).origem());
    }

    @Test
    void buscarConflitos_cardSemData() {
        LocalDate data = LocalDate.of(2026, 5, 24);
        Card card = mock(Card.class);
        when(card.getTitulo()).thenReturn("Evento sem data");
        when(card.getDataEvento()).thenReturn(null);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(1, resultado.conflitos().size());
        assertEquals("Evento sem data", resultado.conflitos().get(0).titulo());
        assertEquals("Sistema", resultado.conflitos().get(0).origem());
    }

    @Test
    void buscarConflitos_demandaSemPrazo() {
        LocalDate data = LocalDate.of(2026, 5, 25);
        Demanda demanda = mock(Demanda.class);
        when(demanda.getTitulo()).thenReturn("Demanda sem prazo");
        when(demanda.getDataPrazo()).thenReturn(null);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of());
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of(demanda));
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(1, resultado.conflitos().size());
        assertEquals("Demanda sem prazo", resultado.conflitos().get(0).titulo());
        assertEquals("Sistema", resultado.conflitos().get(0).origem());
    }

    @Test
    void buscarConflitos_listasVazias() {
        LocalDate data = LocalDate.of(2026, 5, 26);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of());
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertTrue(resultado.conflitos().isEmpty());
        assertTrue(resultado.googleIndisponivel());
    }

    @Test
    void buscarConflitos_multiplosConflitosSimultaneos() {
        LocalDate data = LocalDate.of(2026, 5, 27);
        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        Demanda demanda = mock(Demanda.class);
        when(card1.getTitulo()).thenReturn("Card 1");
        when(card2.getTitulo()).thenReturn("Card 2");
        when(demanda.getTitulo()).thenReturn("Demanda 1");

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card1, card2));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of(demanda));
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(3, resultado.conflitos().size());
        assertTrue(resultado.conflitos().stream().anyMatch(c -> "Card 1".equals(c.titulo())));
        assertTrue(resultado.conflitos().stream().anyMatch(c -> "Card 2".equals(c.titulo())));
        assertTrue(resultado.conflitos().stream().anyMatch(c -> "Demanda 1".equals(c.titulo())));
    }

    @Test
    void buscarConflitos_conflitoParcial() {
        LocalDate data = LocalDate.of(2026, 5, 28);
        Card card = mock(Card.class);
        when(card.getTitulo()).thenReturn("Card com data");
        when(card.getDataEvento()).thenReturn(data);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(null)).thenReturn(false);

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(1, resultado.conflitos().size());
        assertEquals("Card com data", resultado.conflitos().get(0).titulo());
        assertEquals("Sistema", resultado.conflitos().get(0).origem());
    }

    @Test
    void buscarConflitos_conflitoTotal() {
        LocalDate data = LocalDate.of(2026, 5, 29);
        Card card = mock(Card.class);
        when(card.getTitulo()).thenReturn("Card");
        when(card.getDataEvento()).thenReturn(data);

        when(cardRepository.findByDataEventoOrderByTituloAsc(data)).thenReturn(List.of(card));
        when(demandaRepository.findByDataPrazoOrderByTituloAsc(data)).thenReturn(List.of());
        when(googleCalendarService.podeConsultar(null)).thenReturn(true);
        when(googleCalendarService.listarEventos(null, data, data)).thenReturn(List.of());

        AgendaConflictCheckDTO resultado = agendaConflictService.buscarConflitos(null, data, null);

        assertEquals(1, resultado.conflitos().size());
        assertEquals("Card", resultado.conflitos().get(0).titulo());
        assertEquals("Sistema", resultado.conflitos().get(0).origem());
    }
}

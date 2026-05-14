package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final String CALENDAR_READONLY_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<GoogleCalendarEventDTO> listarEventos(OAuth2AuthorizedClient googleClient,
                                                       LocalDate inicio,
                                                       LocalDate fim) {
        if (googleClient == null || googleClient.getAccessToken() == null) {
            return List.of();
        }

        if (!googleClient.getAccessToken().getScopes().contains(CALENDAR_READONLY_SCOPE)) {
            throw new IllegalStateException("Sua sessao Google ainda nao tem permissao de leitura do calendario. Saia e entre novamente.");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(EVENTS_URL)
                .queryParam("timeMin", inicio.atStartOfDay(DEFAULT_ZONE).toInstant().toString())
                .queryParam("timeMax", fim.plusDays(1).atStartOfDay(DEFAULT_ZONE).toInstant().toString())
                .queryParam("singleEvents", true)
                .queryParam("orderBy", "startTime")
                .queryParam("maxResults", 50)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleClient.getAccessToken().getTokenValue());

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );

            JsonNode items = response.getBody() != null ? response.getBody().path("items") : null;
            if (items == null || !items.isArray()) {
                return List.of();
            }

            List<GoogleCalendarEventDTO> eventos = new ArrayList<>();
            for (JsonNode item : items) {
                eventos.add(converterEvento(item));
            }
            return eventos;
        } catch (HttpStatusCodeException ex) {
            String detalhe = extrairMensagemGoogle(ex.getResponseBodyAsString());
            throw new IllegalStateException(
                    "Google Agenda respondeu com erro " + ex.getStatusCode().value() + ": " + detalhe,
                    ex
            );
        } catch (RestClientException ex) {
            throw new IllegalStateException("Nao foi possivel carregar eventos do Google Agenda.", ex);
        }
    }

    private GoogleCalendarEventDTO converterEvento(JsonNode item) {
        JsonNode start = item.path("start");
        JsonNode end = item.path("end");
        boolean diaInteiro = start.hasNonNull("date");
        LocalDate data = diaInteiro
                ? LocalDate.parse(start.path("date").asText())
                : OffsetDateTime.parse(start.path("dateTime").asText()).toLocalDate();

        LocalDateTime inicio = diaInteiro ? null : OffsetDateTime.parse(start.path("dateTime").asText()).toLocalDateTime();
        LocalDateTime fim = diaInteiro || !end.hasNonNull("dateTime")
                ? null
                : OffsetDateTime.parse(end.path("dateTime").asText()).toLocalDateTime();

        String titulo = textoOuPadrao(item.path("summary").asText(null), "Evento sem titulo");
        String descricao = item.path("description").asText(null);
        String local = item.path("location").asText(null);

        return new GoogleCalendarEventDTO(titulo, descricao, local, data, inicio, fim, diaInteiro);
    }

    private String textoOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private String extrairMensagemGoogle(String corpoResposta) {
        if (corpoResposta == null || corpoResposta.isBlank()) {
            return "sem detalhes na resposta.";
        }

        try {
            JsonNode root = objectMapper.readTree(corpoResposta);
            String mensagem = root.path("error").path("message").asText(null);
            return textoOuPadrao(mensagem, "sem detalhes na resposta.");
        } catch (JsonProcessingException ex) {
            return corpoResposta;
        }
    }
}

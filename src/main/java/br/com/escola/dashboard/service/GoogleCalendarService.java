package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.GoogleCalendarEventRequestDTO;
import br.com.escola.dashboard.dto.GoogleCalendarEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoogleCalendarService {

    private static final String EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final String CALENDAR_READONLY_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
    private static final String CALENDAR_EVENTS_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    private static final String CALENDAR_FULL_SCOPE = "https://www.googleapis.com/auth/calendar";
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleCalendarService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean podeConsultar(OAuth2AuthorizedClient googleClient) {
        if (googleClient == null || googleClient.getAccessToken() == null) {
            return false;
        }

        Set<String> scopes = googleClient.getAccessToken().getScopes();
        return scopes.contains(CALENDAR_READONLY_SCOPE)
                || scopes.contains(CALENDAR_EVENTS_SCOPE)
                || scopes.contains(CALENDAR_FULL_SCOPE);
    }

    public List<GoogleCalendarEventDTO> listarEventos(OAuth2AuthorizedClient googleClient,
                                                       LocalDate inicio,
                                                       LocalDate fim) {
        if (googleClient == null || googleClient.getAccessToken() == null) {
            return List.of();
        }

        if (!podeConsultar(googleClient)) {
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
        String tokenValue = googleClient.getAccessToken().getTokenValue();
        if (tokenValue != null) {
            headers.setBearerAuth(tokenValue);
        }

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
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

    public void criarEvento(OAuth2AuthorizedClient googleClient, GoogleCalendarEventRequestDTO requestDTO) {
        if (googleClient == null || googleClient.getAccessToken() == null) {
            throw new IllegalStateException("Sua sessao Google nao esta pronta para criar eventos. Saia e entre novamente.");
        }

        if (!temPermissaoEscrita(googleClient.getAccessToken().getScopes())) {
            throw new IllegalStateException("Sua sessao Google ainda nao tem permissao para criar eventos. Saia, entre novamente e autorize o Google Agenda.");
        }

        if (!requestDTO.getFim().isAfter(requestDTO.getInicio())) {
            throw new IllegalArgumentException("O horario final deve ser posterior ao horario inicial.");
        }

        String inicio = requestDTO.getData()
                .atTime(requestDTO.getInicio())
                .atZone(DEFAULT_ZONE)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String fim = requestDTO.getData()
                .atTime(requestDTO.getFim())
                .atZone(DEFAULT_ZONE)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Map<String, Object> payload = Map.of(
                "summary", requestDTO.getTitulo().trim(),
                "description", StringUtils.hasText(requestDTO.getDescricao()) ? requestDTO.getDescricao().trim() : "",
                "start", Map.of(
                        "dateTime", inicio,
                        "timeZone", DEFAULT_ZONE.getId()
                ),
                "end", Map.of(
                        "dateTime", fim,
                        "timeZone", DEFAULT_ZONE.getId()
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleClient.getAccessToken().getTokenValue());
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.exchange(
                    EVENTS_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    JsonNode.class
            );
        } catch (HttpStatusCodeException ex) {
            String detalhe = extrairMensagemGoogle(ex.getResponseBodyAsString());
            throw new IllegalStateException(
                    "Google Agenda respondeu com erro " + ex.getStatusCode().value() + ": " + detalhe,
                    ex
            );
        } catch (RestClientException ex) {
            throw new IllegalStateException("Nao foi possivel criar o evento no Google Agenda.", ex);
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

    private boolean temPermissaoEscrita(Set<String> scopes) {
        return scopes.contains(CALENDAR_EVENTS_SCOPE) || scopes.contains(CALENDAR_FULL_SCOPE);
    }
}

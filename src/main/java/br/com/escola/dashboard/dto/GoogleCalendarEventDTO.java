package br.com.escola.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoogleCalendarEventDTO {

    private final String titulo;
    private final String descricao;
    private final String local;
    private final LocalDate data;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private final boolean diaInteiro;

    public GoogleCalendarEventDTO(String titulo,
                                  String descricao,
                                  String local,
                                  LocalDate data,
                                  LocalDateTime inicio,
                                  LocalDateTime fim,
                                  boolean diaInteiro) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.data = data;
        this.inicio = inicio;
        this.fim = fim;
        this.diaInteiro = diaInteiro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocal() {
        return local;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public boolean isDiaInteiro() {
        return diaInteiro;
    }
}

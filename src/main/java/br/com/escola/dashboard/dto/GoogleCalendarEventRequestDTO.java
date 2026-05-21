package br.com.escola.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class GoogleCalendarEventRequestDTO {

    @NotBlank(message = "Informe o titulo do evento.")
    @Size(max = 160, message = "O titulo deve ter no maximo 160 caracteres.")
    private String titulo;

    @Size(max = 1000, message = "A descricao deve ter no maximo 1000 caracteres.")
    private String descricao;

    @NotNull(message = "Informe a data do evento.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    @NotNull(message = "Informe o horario de inicio.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime inicio;

    @NotNull(message = "Informe o horario de fim.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime fim;

    private String origem = "admin";

    private String segmento;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public LocalTime getFim() {
        return fim;
    }

    public void setFim(LocalTime fim) {
        this.fim = fim;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }
}

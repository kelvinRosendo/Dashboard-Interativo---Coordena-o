package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DemandaRequestDTO {

    @NotBlank(message = "Informe o titulo da demanda.")
    @Size(max = 160, message = "O titulo deve ter no maximo 160 caracteres.")
    private String titulo;

    @NotBlank(message = "Informe a descricao da demanda.")
    private String descricao;

    @NotNull(message = "Selecione a coordenacao.")
    private SegmentoCoordenacao segmento;

    @NotNull(message = "Selecione a prioridade.")
    private PrioridadeDemanda prioridade;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPrazo;

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

    public SegmentoCoordenacao getSegmento() {
        return segmento;
    }

    public void setSegmento(SegmentoCoordenacao segmento) {
        this.segmento = segmento;
    }

    public PrioridadeDemanda getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeDemanda prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataPrazo() {
        return dataPrazo;
    }

    public void setDataPrazo(LocalDate dataPrazo) {
        this.dataPrazo = dataPrazo;
    }
}

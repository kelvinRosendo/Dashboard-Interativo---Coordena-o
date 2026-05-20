package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandas")
public class Demanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SegmentoCoordenacao segmento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeDemanda prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDemanda status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDate dataPrazo;

    private String criadaPor;

    private LocalDateTime concluidaEm;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean visualizadaPelaCoordenadora;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }

        if (status == null) {
            status = StatusDemanda.PENDENTE;
        }

        atualizarConclusao();
    }

    @PreUpdate
    public void preUpdate() {
        atualizarConclusao();
    }

    private void atualizarConclusao() {
        if (status == StatusDemanda.CONCLUIDA && concluidaEm == null) {
            concluidaEm = LocalDateTime.now();
        }

        if (status != StatusDemanda.CONCLUIDA) {
            concluidaEm = null;
        }
    }

    public Long getId() {
        return id;
    }

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

    public StatusDemanda getStatus() {
        return status;
    }

    public void setStatus(StatusDemanda status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataPrazo() {
        return dataPrazo;
    }

    public void setDataPrazo(LocalDate dataPrazo) {
        this.dataPrazo = dataPrazo;
    }

    public String getCriadaPor() {
        return criadaPor;
    }

    public void setCriadaPor(String criadaPor) {
        this.criadaPor = criadaPor;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }

    public void setConcluidaEm(LocalDateTime concluidaEm) {
        this.concluidaEm = concluidaEm;
    }

    public boolean isVisualizadaPelaCoordenadora() {
        return visualizadaPelaCoordenadora;
    }

    public void setVisualizadaPelaCoordenadora(boolean visualizadaPelaCoordenadora) {
        this.visualizadaPelaCoordenadora = visualizadaPelaCoordenadora;
    }
}

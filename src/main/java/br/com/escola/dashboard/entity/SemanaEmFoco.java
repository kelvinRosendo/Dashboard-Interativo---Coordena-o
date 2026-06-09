package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "semanas_em_foco")
public class SemanaEmFoco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Selecione o segmento em destaque.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SegmentoCoordenacao segmento;

    @NotBlank(message = "O titulo e obrigatorio.")
    @Size(max = 150, message = "O titulo pode ter no maximo 150 caracteres.")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "A descricao e obrigatoria.")
    @Size(max = 1000, message = "A descricao pode ter no maximo 1000 caracteres.")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NotNull(message = "Selecione a prioridade.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeDemanda prioridade;

    @NotNull(message = "Informe a data de inicio.")
    @Column(nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "Informe a data de fim.")
    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @OneToOne(mappedBy = "semanaEmFoco")
    private RelatorioSemanaEmFoco relatorio;

    @PrePersist
    public void prePersist() {
        if (atualizadoEm == null) {
            atualizadoEm = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SegmentoCoordenacao getSegmento() {
        return segmento;
    }

    public void setSegmento(SegmentoCoordenacao segmento) {
        this.segmento = segmento;
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

    public PrioridadeDemanda getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeDemanda prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public RelatorioSemanaEmFoco getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatorioSemanaEmFoco relatorio) {
        this.relatorio = relatorio;
    }
}

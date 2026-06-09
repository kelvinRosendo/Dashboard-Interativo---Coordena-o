package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.StatusRelatorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorios_semana_em_foco",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "semana_em_foco_id", name = "uk_relatorio_semana")
       })
public class RelatorioSemanaEmFoco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Semana em Foco e obrigatoria.")
    @ManyToOne
    @JoinColumn(name = "semana_em_foco_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_relatorio_semana"))
    private SemanaEmFoco semanaEmFoco;

    @NotBlank(message = "ID da coordenadora e obrigatorio.")
    @Column(nullable = false, length = 255)
    private String coordenadoraId;

    @NotBlank(message = "Nome da coordenadora e obrigatorio.")
    @Column(nullable = false, length = 150)
    private String coordenadoraNome;

    @NotBlank(message = "Email da coordenadora e obrigatorio.")
    @Column(nullable = false, length = 150)
    private String coordenadoraEmail;

    @NotNull(message = "Data de inicio da semana e obrigatoria.")
    @Column(nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim da semana e obrigatoria.")
    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(columnDefinition = "TEXT")
    private String resumoSemana;

    @Column(columnDefinition = "TEXT")
    private String atividadesExecutadas;

    @Column(columnDefinition = "TEXT")
    private String pendencias;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(columnDefinition = "TEXT")
    private String conclusao;

    @NotNull(message = "Status do relatorio e obrigatorio.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRelatorio status = StatusRelatorio.RASCUNHO;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    private LocalDateTime finalizadoEm;

    @Column(length = 255)
    private String finalizadoPor;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
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

    public SemanaEmFoco getSemanaEmFoco() {
        return semanaEmFoco;
    }

    public void setSemanaEmFoco(SemanaEmFoco semanaEmFoco) {
        this.semanaEmFoco = semanaEmFoco;
    }

    public String getCoordenadoraId() {
        return coordenadoraId;
    }

    public void setCoordenadoraId(String coordenadoraId) {
        this.coordenadoraId = coordenadoraId;
    }

    public String getCoordenadoraNome() {
        return coordenadoraNome;
    }

    public void setCoordenadoraNome(String coordenadoraNome) {
        this.coordenadoraNome = coordenadoraNome;
    }

    public String getCoordenadoraEmail() {
        return coordenadoraEmail;
    }

    public void setCoordenadoraEmail(String coordenadoraEmail) {
        this.coordenadoraEmail = coordenadoraEmail;
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

    public String getResumoSemana() {
        return resumoSemana;
    }

    public void setResumoSemana(String resumoSemana) {
        this.resumoSemana = resumoSemana;
    }

    public String getAtividadesExecutadas() {
        return atividadesExecutadas;
    }

    public void setAtividadesExecutadas(String atividadesExecutadas) {
        this.atividadesExecutadas = atividadesExecutadas;
    }

    public String getPendencias() {
        return pendencias;
    }

    public void setPendencias(String pendencias) {
        this.pendencias = pendencias;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getConclusao() {
        return conclusao;
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    public StatusRelatorio getStatus() {
        return status;
    }

    public void setStatus(StatusRelatorio status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getFinalizadoEm() {
        return finalizadoEm;
    }

    public void setFinalizadoEm(LocalDateTime finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public String getFinalizadoPor() {
        return finalizadoPor;
    }

    public void setFinalizadoPor(String finalizadoPor) {
        this.finalizadoPor = finalizadoPor;
    }
}

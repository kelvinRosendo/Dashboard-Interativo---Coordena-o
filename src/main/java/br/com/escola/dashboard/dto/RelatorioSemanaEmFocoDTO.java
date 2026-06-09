package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.StatusRelatorio;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class RelatorioSemanaEmFocoDTO {

    private Long id;

    @NotNull(message = "Semana em Foco ID e obrigatoria.")
    private Long semanaEmFocoId;

    private String coordenadoraId;

    private String coordenadoraNome;

    private String coordenadoraEmail;

    private String resumoSemana;

    private String atividadesExecutadas;

    private String pendencias;

    private String observacoes;

    private String conclusao;

    private StatusRelatorio status;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private LocalDateTime finalizadoEm;

    private String finalizadoPor;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSemanaEmFocoId() {
        return semanaEmFocoId;
    }

    public void setSemanaEmFocoId(Long semanaEmFocoId) {
        this.semanaEmFocoId = semanaEmFocoId;
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

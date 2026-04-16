package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CardRequestDTO {

    @NotBlank(message = "Informe um titulo para o card.")
    @Size(max = 120, message = "O titulo pode ter no maximo 120 caracteres.")
    private String titulo;

    @Size(max = 1000, message = "A descricao pode ter no maximo 1000 caracteres.")
    private String descricao;

    @NotNull(message = "Selecione o tipo de card.")
    private CategoriaCard categoria;

    @NotNull(message = "Selecione a prioridade.")
    private PrioridadeCard prioridade;

    private LocalDate dataEvento;

    @Size(max = 120, message = "O nome do responsavel pode ter no maximo 120 caracteres.")
    private String responsavel;

    @NotNull(message = "Selecione o status.")
    private StatusCard status;

    @Size(max = 1000, message = "As observacoes podem ter no maximo 1000 caracteres.")
    private String observacoes;

    public CardRequestDTO() {
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

    public CategoriaCard getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCard categoria) {
        this.categoria = categoria;
    }

    public PrioridadeCard getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeCard prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public StatusCard getStatus() {
        return status;
    }

    public void setStatus(StatusCard status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

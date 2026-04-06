package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;

import java.time.LocalDate;

/**
 * 🔹 CardRequestDTO
 *
 * Esse DTO representa os dados que o usuário envia para o sistema
 * ao criar ou atualizar um card.
 *
 * Ele funciona como a "entrada" de dados da aplicação.
 */
public class CardRequestDTO {

    /**
     * 🔹 Título do card/evento
     */
    private String titulo;

    /**
     * 🔹 Descrição principal
     */
    private String descricao;

    /**
     * 🔹 Categoria do card
     * Exemplo: EVENTO, AVISO, TAREFA
     */
    private CategoriaCard categoria;

    /**
     * 🔹 Prioridade do card
     * Exemplo: BAIXA, MEDIA, ALTA
     */
    private PrioridadeCard prioridade;

    /**
     * 🔹 Data em que o evento vai acontecer
     */
    private LocalDate dataEvento;

    /**
     * 🔹 Nome do responsável pelo card/evento
     */
    private String responsavel;

    /**
     * 🔹 Status atual do card
     * Exemplo: PENDENTE, EM_ANDAMENTO, CONCLUIDO
     */
    private StatusCard status;

    /**
     * 🔹 Observações extras
     */
    private String observacoes;

    /**
     * 🔹 Construtor vazio
     *
     * Necessário para o Spring conseguir criar o objeto automaticamente.
     */
    public CardRequestDTO() {
    }

    /**
     * 🔹 Getter do título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * 🔹 Setter do título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * 🔹 Getter da descrição
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * 🔹 Setter da descrição
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * 🔹 Getter da categoria
     */
    public CategoriaCard getCategoria() {
        return categoria;
    }

    /**
     * 🔹 Setter da categoria
     */
    public void setCategoria(CategoriaCard categoria) {
        this.categoria = categoria;
    }

    /**
     * 🔹 Getter da prioridade
     */
    public PrioridadeCard getPrioridade() {
        return prioridade;
    }

    /**
     * 🔹 Setter da prioridade
     */
    public void setPrioridade(PrioridadeCard prioridade) {
        this.prioridade = prioridade;
    }

    /**
     * 🔹 Getter da data do evento
     */
    public LocalDate getDataEvento() {
        return dataEvento;
    }

    /**
     * 🔹 Setter da data do evento
     */
    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    /**
     * 🔹 Getter do responsável
     */
    public String getResponsavel() {
        return responsavel;
    }

    /**
     * 🔹 Setter do responsável
     */
    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    /**
     * 🔹 Getter do status
     */
    public StatusCard getStatus() {
        return status;
    }

    /**
     * 🔹 Setter do status
     */
    public void setStatus(StatusCard status) {
        this.status = status;
    }

    /**
     * 🔹 Getter das observações
     */
    public String getObservacoes() {
        return observacoes;
    }

    /**
     * 🔹 Setter das observações
     */
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
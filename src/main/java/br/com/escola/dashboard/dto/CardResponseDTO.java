package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 🔹 CardResponseDTO
 *
 * Esse DTO representa os dados que o sistema devolve como resposta
 * para a tela ou para outros componentes da aplicação.
 *
 * Ele funciona como a "saída" de dados da aplicação.
 */
public class CardResponseDTO {

    /**
     * 🔹 ID do card
     */
    private Long id;

    /**
     * 🔹 Título do card
     */
    private String titulo;

    /**
     * 🔹 Descrição do card
     */
    private String descricao;

    /**
     * 🔹 Categoria do card
     */
    private CategoriaCard categoria;

    /**
     * 🔹 Prioridade do card
     */
    private PrioridadeCard prioridade;

    /**
     * 🔹 Data em que o card foi criado
     */
    private LocalDateTime dataCriacao;

    /**
     * 🔹 Data em que o evento vai acontecer
     */
    private LocalDate dataEvento;

    /**
     * 🔹 Responsável pelo card/evento
     */
    private String responsavel;

    /**
     * 🔹 Status atual do card
     */
    private StatusCard status;

    /**
     * 🔹 Observações extras
     */
    private String observacoes;

    /**
     * 🔹 Construtor vazio
     */
    public CardResponseDTO() {
    }

    /**
     * 🔹 Construtor completo
     *
     * Pode ser útil se você quiser criar o DTO já preenchido de uma vez.
     */
    public CardResponseDTO(Long id,
                           String titulo,
                           String descricao,
                           CategoriaCard categoria,
                           PrioridadeCard prioridade,
                           LocalDateTime dataCriacao,
                           LocalDate dataEvento,
                           String responsavel,
                           StatusCard status,
                           String observacoes) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
        this.dataEvento = dataEvento;
        this.responsavel = responsavel;
        this.status = status;
        this.observacoes = observacoes;
    }

    /**
     * 🔹 Getter do ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 🔹 Setter do ID
     */
    public void setId(Long id) {
        this.id = id;
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
     * 🔹 Getter da data de criação
     */
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    /**
     * 🔹 Setter da data de criação
     */
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
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
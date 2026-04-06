package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.StatusCard;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 🔹 Card
 *
 * Essa classe representa a entidade principal do sistema.
 * Ela define como os dados do card serão salvos no banco de dados.
 */
@Entity
@Table(name = "cards")
public class Card {

    /**
     * 🔹 ID único do card
     *
     * É gerado automaticamente pelo banco.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🔹 Título do card
     *
     * Exemplo:
     * "Reunião pedagógica"
     */
    @Column(nullable = false)
    private String titulo;

    /**
     * 🔹 Descrição do card
     *
     * Campo usado para detalhar a informação principal.
     */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * 🔹 Categoria do card
     *
     * Exemplo:
     * EVENTO, AVISO, TAREFA
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCard categoria;

    /**
     * 🔹 Prioridade do card
     *
     * Exemplo:
     * BAIXA, MEDIA, ALTA
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeCard prioridade;

    /**
     * 🔹 Data de criação do card
     *
     * Registra quando o card foi cadastrado no sistema.
     */
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * 🔹 Data do evento
     *
     * Representa o dia em que o evento ou atividade acontecerá.
     */
    private LocalDate dataEvento;

    /**
     * 🔹 Responsável
     *
     * Representa quem está responsável pelo item.
     */
    private String responsavel;

    /**
     * 🔹 Status do card
     *
     * Usa o enum StatusCard para manter os valores padronizados.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCard status;

    /**
     * 🔹 Observações
     *
     * Campo para informações extras e anotações importantes.
     */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /**
     * 🔹 Construtor vazio
     *
     * Obrigatório para o JPA/Hibernate funcionar corretamente.
     */
    public Card() {
    }

    /**
     * 🔹 Getter do ID
     */
    public Long getId() {
        return id;
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
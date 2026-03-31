package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCard categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeCard prioridade;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    // 🔹 Construtor vazio (obrigatório pro JPA)
    public Card() {
    }

    // 🔹 Construtor completo
    public Card(String titulo, String descricao, CategoriaCard categoria, PrioridadeCard prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.dataCriacao = LocalDateTime.now();
    }

    // 🔹 Getters e Setters

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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
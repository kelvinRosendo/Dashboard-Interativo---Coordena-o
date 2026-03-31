package br.com.escola.dashboard.dto;

// Importa os enums
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;

import java.time.LocalDateTime;

/**
 * 🔹 CardResponseDTO
 * 
 * Esse DTO representa os dados que o sistema envia como resposta
 * para o usuário após uma operação (listar, buscar, etc).
 */
public class CardResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private CategoriaCard categoria;
    private PrioridadeCard prioridade;
    private LocalDateTime dataCriacao;

    // 🔹 Construtor vazio
    public CardResponseDTO() {
    }

    // 🔹 Construtor completo (opcional, mas útil)
    public CardResponseDTO(Long id, String titulo, String descricao,
                           CategoriaCard categoria, PrioridadeCard prioridade,
                           LocalDateTime dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
    }

    // 🔹 Getters e Setters

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public CategoriaCard getCategoria() {
        return categoria;
    }

    public PrioridadeCard getPrioridade() {
        return prioridade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCategoria(CategoriaCard categoria) {
        this.categoria = categoria;
    }

    public void setPrioridade(PrioridadeCard prioridade) {
        this.prioridade = prioridade;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
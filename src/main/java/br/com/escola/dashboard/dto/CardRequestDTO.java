package br.com.escola.dashboard.dto;

// Importa os enums que já criamos
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;

/**
 * 🔹 CardRequestDTO
 * 
 * Esse DTO representa os dados que o usuário envia para o sistema
 * ao criar ou atualizar um Card.
 */
public class CardRequestDTO {

    private String titulo;
    private String descricao;
    private CategoriaCard categoria;
    private PrioridadeCard prioridade;

    // 🔹 Construtor vazio (necessário para o Spring)
    public CardRequestDTO() {
    }

    // 🔹 Getters e Setters

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
}
package br.com.escola.dashboard.enums;

public enum StatusUsuario {
    ATIVO("Ativo", "ativo"),
    PENDENTE("Pendente", "pendente"),
    BLOQUEADO("Bloqueado", "bloqueado");

    private final String titulo;
    private final String cssClass;

    StatusUsuario(String titulo, String cssClass) {
        this.titulo = titulo;
        this.cssClass = cssClass;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCssClass() {
        return cssClass;
    }
}

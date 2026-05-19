package br.com.escola.dashboard.enums;

public enum StatusDemanda {
    PENDENTE("Pendente", "pendente"),
    EM_ANDAMENTO("Em andamento", "andamento"),
    CONCLUIDA("Concluida", "concluida"),
    CANCELADA("Cancelada", "cancelada");

    private final String titulo;
    private final String cssClass;

    StatusDemanda(String titulo, String cssClass) {
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

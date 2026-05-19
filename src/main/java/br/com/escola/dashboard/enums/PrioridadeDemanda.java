package br.com.escola.dashboard.enums;

public enum PrioridadeDemanda {
    BAIXA("Baixa", "baixa"),
    MEDIA("Media", "media"),
    ALTA("Alta", "alta"),
    URGENTE("Urgente", "urgente");

    private final String titulo;
    private final String cssClass;

    PrioridadeDemanda(String titulo, String cssClass) {
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

package br.com.escola.dashboard.enums;

public enum StatusRelatorio {

    RASCUNHO("Rascunho"),
    FINALIZADO("Finalizado");

    private final String label;

    StatusRelatorio(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

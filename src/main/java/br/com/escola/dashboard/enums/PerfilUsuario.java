package br.com.escola.dashboard.enums;

public enum PerfilUsuario {
    ADMIN("Administradora", "Acesso total ao sistema"),
    VICE_DIRETORA("Vice-Diretora", "Visualizacao geral de todos os segmentos"),
    COORDENADORA("Coordenadora", "Acesso restrito aos segmentos vinculados");

    private final String titulo;
    private final String descricao;

    PerfilUsuario(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}

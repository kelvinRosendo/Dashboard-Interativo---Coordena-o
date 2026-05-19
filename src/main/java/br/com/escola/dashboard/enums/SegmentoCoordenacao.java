package br.com.escola.dashboard.enums;

import java.util.Arrays;

public enum SegmentoCoordenacao {
    EDUCACAO_INFANTIL("educacao-infantil", "Educacao Infantil", "Demandas de rotina e acompanhamento do Infantil.", "infantil"),
    FUNDAMENTAL_1("fundamental-1", "Fundamental 1", "Demandas de rotina e acompanhamento do Fundamental 1.", "fund1"),
    FUNDAMENTAL_2("fundamental-2", "Fundamental 2", "Demandas de rotina e acompanhamento do Fundamental 2.", "fund2"),
    ENSINO_MEDIO("ensino-medio", "Ensino Medio", "Demandas de rotina e acompanhamento do Ensino Medio.", "medio");

    private final String slug;
    private final String titulo;
    private final String descricao;
    private final String[] aliases;

    SegmentoCoordenacao(String slug, String titulo, String descricao, String... aliases) {
        this.slug = slug;
        this.titulo = titulo;
        this.descricao = descricao;
        this.aliases = aliases;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SegmentoCoordenacao fromSlug(String slug) {
        if (slug == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(segmento -> segmento.slug.equalsIgnoreCase(slug)
                        || Arrays.stream(segmento.aliases).anyMatch(alias -> alias.equalsIgnoreCase(slug)))
                .findFirst()
                .orElse(null);
    }
}

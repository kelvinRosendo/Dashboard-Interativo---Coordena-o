package br.com.escola.dashboard.dto;

import java.time.LocalDate;

public record SemanaFocoDTO(
        Long id,
        String titulo,
        String descricao,
        String segmento,
        String segmentoTitulo,
        String prioridade,
        LocalDate dataInicio,
        LocalDate dataFim,
        String periodoFormatado,
        boolean ativa,
        String statusRelatorio
) {
    public SemanaFocoDTO {
    }

    public static SemanaFocoDTO vazio() {
        return new SemanaFocoDTO(null, "", "", "", "", "", null, null, "", false, "NAO_INICIADO");
    }
}

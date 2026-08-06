package br.com.escola.dashboard.dto;

import java.time.LocalDate;

public record EventoDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean diaInteiro,
        String segmento,
        String segmentoTitulo,
        String googleEventId,
        String dataFormatada,
        boolean compartilhado
) {
    public EventoDTO {
    }
}

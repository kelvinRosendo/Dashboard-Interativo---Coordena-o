package br.com.escola.dashboard.dto;

import java.time.LocalDateTime;

public record AvisoDTO(
        Long id,
        String titulo,
        String conteudo,
        String prioridade,
        String prioridadeCssClass,
        String segmento,
        String segmentoTitulo,
        LocalDateTime dataCriacao,
        String dataFormatada,
        boolean global
) {
    public AvisoDTO {
    }
}

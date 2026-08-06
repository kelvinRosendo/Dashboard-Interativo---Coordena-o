package br.com.escola.dashboard.dto;

import java.time.LocalDate;

public record DemandaResumoDTO(
        Long id,
        String titulo,
        String descricao,
        String segmento,
        String segmentoCssClass,
        String prioridade,
        String prioridadeCssClass,
        String status,
        String statusCssClass,
        LocalDate dataPrazo,
        String prazoFormatado,
        String criadaPor,
        boolean atrasada
) {
    public DemandaResumoDTO {
    }
}

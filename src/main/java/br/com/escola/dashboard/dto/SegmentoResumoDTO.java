package br.com.escola.dashboard.dto;

public record SegmentoResumoDTO(
        Long id,
        String slug,
        String titulo,
        String descricao,
        long totalDemandas,
        long demandasPendentes,
        long demandasEmAndamento,
        long demandasConcluidas
) {
    public SegmentoResumoDTO {
    }
}

package br.com.escola.dashboard.dto;

public record IndicadoresDTO(
        long demandasAbertas,
        long demandasAtrasadas,
        long demandasConcluidas,
        long demandasEmAndamento,
        long totalDemandas,
        long usuariosAtivos,
        long usuariosPendentes,
        long totalUsuarios,
        long importacoesRealizadas
) {
    public IndicadoresDTO {
    }

    public static IndicadoresDTO vazio() {
        return new IndicadoresDTO(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}

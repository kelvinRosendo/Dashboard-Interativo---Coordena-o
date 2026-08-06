package br.com.escola.dashboard.dto;

public record PendenciasDTO(
        long demandasPendentes,
        long demandasAtrasadas,
        long semanasNaoRelatadas
) {
    public PendenciasDTO {
    }

    public static PendenciasDTO vazio() {
        return new PendenciasDTO(0, 0, 0);
    }
}

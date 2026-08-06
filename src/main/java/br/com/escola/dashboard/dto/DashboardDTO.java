package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.PerfilUsuario;

import java.util.List;

public record DashboardDTO(
        PerfilUsuario perfil,
        String nome,
        String email,
        IndicadoresDTO indicadores,
        SemanaFocoDTO semanaEmFoco,
        List<DemandaResumoDTO> demandas,
        List<ComunicadoDTO> comunicados,
        List<AvisoDTO> avisos,
        List<EventoDTO> eventos,
        List<SegmentoResumoDTO> segmentos,
        PendenciasDTO pendencias
) {
    public DashboardDTO {
    }
}

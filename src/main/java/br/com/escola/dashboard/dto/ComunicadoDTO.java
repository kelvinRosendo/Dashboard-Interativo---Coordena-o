package br.com.escola.dashboard.dto;

import java.time.LocalDateTime;

public record ComunicadoDTO(
        Long id,
        String titulo,
        String conteudo,
        LocalDateTime dataCriacao,
        String dataFormatada
) {
    public ComunicadoDTO {
    }
}

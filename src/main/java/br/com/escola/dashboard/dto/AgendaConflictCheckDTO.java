package br.com.escola.dashboard.dto;

import java.util.List;

public record AgendaConflictCheckDTO(
        List<AgendaConflictDTO> conflitos,
        boolean googleConsultado,
        boolean googleIndisponivel,
        String avisoGoogle
) {
    public boolean temConflitos() {
        return conflitos != null && !conflitos.isEmpty();
    }
}

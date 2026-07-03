package br.com.escola.dashboard.utils;

import br.com.escola.dashboard.dto.AgendaConflictCheckDTO;
import org.springframework.ui.Model;

public final class ConflitoModelHelper {

    private ConflitoModelHelper() {
    }

    public static void adicionarConflitosAoModelo(Model model, AgendaConflictCheckDTO conflitos) {
        model.addAttribute("exibirModalConflito", true);
        model.addAttribute("conflitos", conflitos.conflitos());
        model.addAttribute("googleAgendaIndisponivel", conflitos.googleIndisponivel());
        model.addAttribute("avisoGoogle", conflitos.avisoGoogle());
    }
}

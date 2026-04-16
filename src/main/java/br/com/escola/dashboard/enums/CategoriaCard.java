package br.com.escola.dashboard.enums;

public enum CategoriaCard {
    EVENTO("Novo Evento", "Card Principal - Calendario de Eventos"),
    FALTA_PROFESSOR("Falta de Professores", "Agenda de Falta dos Professores"),
    HORARIO_PROFESSOR("Horario de Professores", "Horarios Professores - Rotativo"),
    ROTINA_ADMINISTRATIVA("Rotina Administrativa", "Rotina Administrativa"),
    ROTINA_AUXILIAR("Rotina Auxiliares", "Rotina Auxiliares"),
    AVISO_NOTA("Avisos / Notas", "Avisos / Notas");

    private final String tituloFormulario;
    private final String tituloDashboard;

    CategoriaCard(String tituloFormulario, String tituloDashboard) {
        this.tituloFormulario = tituloFormulario;
        this.tituloDashboard = tituloDashboard;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public String getTituloDashboard() {
        return tituloDashboard;
    }
}

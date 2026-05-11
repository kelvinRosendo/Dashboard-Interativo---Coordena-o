package br.com.escola.dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public class CalendarioDiaDTO {

    private final LocalDate data;
    private final boolean hoje;
    private final boolean foraDoMes;
    private final List<CardResponseDTO> itens;

    public CalendarioDiaDTO(LocalDate data, boolean hoje, boolean foraDoMes, List<CardResponseDTO> itens) {
        this.data = data;
        this.hoje = hoje;
        this.foraDoMes = foraDoMes;
        this.itens = itens;
    }

    public LocalDate getData() {
        return data;
    }

    public int getDiaDoMes() {
        return data.getDayOfMonth();
    }

    public boolean isHoje() {
        return hoje;
    }

    public boolean isForaDoMes() {
        return foraDoMes;
    }

    public List<CardResponseDTO> getItens() {
        return itens;
    }
}

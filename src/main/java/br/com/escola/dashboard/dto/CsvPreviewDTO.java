package br.com.escola.dashboard.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvPreviewDTO {

    private String tipoEntidade;
    private String nomeArquivo;
    private int totalLinhas;
    private List<String> colunas = new ArrayList<>();
    private List<Map<String, String>> linhasPreview = new ArrayList<>();
    private List<String> errosValidacao = new ArrayList<>();

    public CsvPreviewDTO() {
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public int getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(int totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public List<String> getColunas() {
        return colunas;
    }

    public void setColunas(List<String> colunas) {
        this.colunas = colunas;
    }

    public List<Map<String, String>> getLinhasPreview() {
        return linhasPreview;
    }

    public void setLinhasPreview(List<Map<String, String>> linhasPreview) {
        this.linhasPreview = linhasPreview;
    }

    public List<String> getErrosValidacao() {
        return errosValidacao;
    }

    public void setErrosValidacao(List<String> errosValidacao) {
        this.errosValidacao = errosValidacao;
    }

    public boolean temErros() {
        return !errosValidacao.isEmpty();
    }
}

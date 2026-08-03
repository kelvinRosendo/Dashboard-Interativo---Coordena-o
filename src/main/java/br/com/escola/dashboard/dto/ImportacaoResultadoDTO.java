package br.com.escola.dashboard.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportacaoResultadoDTO {

    private String tipoEntidade;
    private int totalRegistros;
    private int inseridos;
    private int atualizados;
    private int ignorados;
    private long tempoProcessamentoMs;
    private List<String> erros = new ArrayList<>();
    private List<ErroDetalhado> errosDetalhados = new ArrayList<>();

    public ImportacaoResultadoDTO() {
    }

    public ImportacaoResultadoDTO(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public int getInseridos() {
        return inseridos;
    }

    public void setInseridos(int inseridos) {
        this.inseridos = inseridos;
    }

    public int getAtualizados() {
        return atualizados;
    }

    public void setAtualizados(int atualizados) {
        this.atualizados = atualizados;
    }

    public int getIgnorados() {
        return ignorados;
    }

    public void setIgnorados(int ignorados) {
        this.ignorados = ignorados;
    }

    public long getTempoProcessamentoMs() {
        return tempoProcessamentoMs;
    }

    public void setTempoProcessamentoMs(long tempoProcessamentoMs) {
        this.tempoProcessamentoMs = tempoProcessamentoMs;
    }

    public String getTempoFormatado() {
        double segundos = tempoProcessamentoMs / 1000.0;
        if (segundos < 1) {
            return String.format("%.0f ms", (double) tempoProcessamentoMs);
        }
        return String.format("%.1f segundos", segundos);
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }

    public void adicionarErro(String erro) {
        this.erros.add(erro);
    }

    public List<ErroDetalhado> getErrosDetalhados() {
        return errosDetalhados;
    }

    public void setErrosDetalhados(List<ErroDetalhado> errosDetalhados) {
        this.errosDetalhados = errosDetalhados;
    }

    public void adicionarErroDetalhado(int linha, String campo, String erro, String valorRecebido) {
        this.errosDetalhados.add(new ErroDetalhado(linha, campo, erro, valorRecebido));
    }

    public boolean temErros() {
        return !erros.isEmpty() || !errosDetalhados.isEmpty();
    }

    public int getTotalErros() {
        return erros.size() + errosDetalhados.size();
    }

    public String gerarCsvErros() {
        StringBuilder sb = new StringBuilder();
        sb.append("Linha;Erro;Campo;Valor Recebido\n");
        for (ErroDetalhado e : errosDetalhados) {
            sb.append(e.linha()).append(";")
              .append(e.erro()).append(";")
              .append(e.campo()).append(";")
              .append(e.valorRecebido()).append("\n");
        }
        for (String erro : erros) {
            sb.append("-;").append(erro).append(";;\n");
        }
        return sb.toString();
    }

    public record ErroDetalhado(int linha, String campo, String erro, String valorRecebido) {
    }
}

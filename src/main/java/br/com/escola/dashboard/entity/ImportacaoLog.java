package br.com.escola.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_logs")
public class ImportacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoEntidade;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private int totalRegistros;

    @Column(nullable = false)
    private int inseridos;

    @Column(nullable = false)
    private int atualizados;

    @Column(nullable = false)
    private int ignorados;

    @Column(nullable = false)
    private int totalErros;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private double tempoProcessamentoMs;

    @Column(columnDefinition = "TEXT")
    private String errosDetalhados;

    @Column(nullable = false)
    private LocalDateTime dataImportacao;

    @PrePersist
    public void prePersist() {
        if (dataImportacao == null) {
            dataImportacao = LocalDateTime.now();
        }
    }

    public ImportacaoLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public int getTotalErros() {
        return totalErros;
    }

    public void setTotalErros(int totalErros) {
        this.totalErros = totalErros;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTempoProcessamentoMs() {
        return tempoProcessamentoMs;
    }

    public void setTempoProcessamentoMs(double tempoProcessamentoMs) {
        this.tempoProcessamentoMs = tempoProcessamentoMs;
    }

    public String getErrosDetalhados() {
        return errosDetalhados;
    }

    public void setErrosDetalhados(String errosDetalhados) {
        this.errosDetalhados = errosDetalhados;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public String getTempoFormatado() {
        double segundos = tempoProcessamentoMs / 1000.0;
        if (segundos < 1) {
            return String.format("%.0f ms", tempoProcessamentoMs);
        }
        return String.format("%.1f segundos", segundos);
    }
}

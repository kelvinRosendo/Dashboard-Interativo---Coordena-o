package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String fotoUrl;
    private PerfilUsuario perfil;
    private StatusUsuario status;
    private Boolean ativo;
    private LocalDateTime ultimoLogin;
    private LocalDateTime dataCriacao;
    private List<String> segmentos;

    public UsuarioResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<String> getSegmentos() {
        return segmentos;
    }

    public void setSegmentos(List<String> segmentos) {
        this.segmentos = segmentos;
    }
}

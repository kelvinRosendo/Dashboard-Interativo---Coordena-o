package br.com.escola.dashboard.entity;

import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerfilUsuario perfil = PerfilUsuario.COORDENADORA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusUsuario status = StatusUsuario.PENDENTE;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (dataAtualizacao == null) {
            dataAtualizacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public boolean isAdmin() {
        return perfil == PerfilUsuario.ADMIN;
    }

    public boolean isViceDiretora() {
        return perfil == PerfilUsuario.VICE_DIRETORA;
    }

    public boolean isCoordenadora() {
        return perfil == PerfilUsuario.COORDENADORA;
    }

    public boolean isAtivo() {
        return status == StatusUsuario.ATIVO;
    }

    public boolean isPendente() {
        return status == StatusUsuario.PENDENTE;
    }

    public boolean isBloqueado() {
        return status == StatusUsuario.BLOQUEADO;
    }
}

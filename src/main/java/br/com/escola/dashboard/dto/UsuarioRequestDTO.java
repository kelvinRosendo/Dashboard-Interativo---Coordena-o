package br.com.escola.dashboard.dto;

import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UsuarioRequestDTO {

    @NotNull(message = "Informe o nome do usuario.")
    private String nome;

    @NotNull(message = "Selecione o perfil do usuario.")
    private PerfilUsuario perfil;

    @NotNull(message = "Selecione o status do usuario.")
    private StatusUsuario status;

    private List<Long> segmentoIds;

    public UsuarioRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public List<Long> getSegmentoIds() {
        return segmentoIds;
    }

    public void setSegmentoIds(List<Long> segmentoIds) {
        this.segmentoIds = segmentoIds;
    }
}

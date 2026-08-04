package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.repository.SegmentoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PerfilService {

    private final UsuarioService usuarioService;
    private final SegmentoRepository segmentoRepository;

    public PerfilService(UsuarioService usuarioService, SegmentoRepository segmentoRepository) {
        this.usuarioService = usuarioService;
        this.segmentoRepository = segmentoRepository;
    }

    public boolean isAdmin(Usuario usuario) {
        return usuario != null && usuario.getPerfil() == PerfilUsuario.ADMIN;
    }

    public boolean isViceDiretora(Usuario usuario) {
        return usuario != null && usuario.getPerfil() == PerfilUsuario.VICE_DIRETORA;
    }

    public boolean isCoordenadora(Usuario usuario) {
        return usuario != null && usuario.getPerfil() == PerfilUsuario.COORDENADORA;
    }

    public boolean isAdminOrViceDiretora(Usuario usuario) {
        return usuario != null && (usuario.getPerfil() == PerfilUsuario.ADMIN
                || usuario.getPerfil() == PerfilUsuario.VICE_DIRETORA);
    }

    public List<Segmento> getSegmentosDoUsuario(Usuario usuario) {
        if (usuario == null) {
            return List.of();
        }
        if (usuario.isAdmin() || usuario.isViceDiretora()) {
            return segmentoRepository.findByAtivoTrueOrderByTitulo();
        }
        return usuarioService.buscarSegmentosDoUsuario(usuario.getId());
    }

    public List<Segmento> getSegmentosDoUsuarioPorEmail(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return getSegmentosDoUsuario(usuario);
    }

    public String getDashboardRedirect(Usuario usuario) {
        if (usuario == null) {
            return "/login";
        }
        return switch (usuario.getPerfil()) {
            case ADMIN -> "/admin";
            case VICE_DIRETORA -> "/vice-diretora";
            case COORDENADORA -> "/coordenadora/dashboard";
        };
    }
}

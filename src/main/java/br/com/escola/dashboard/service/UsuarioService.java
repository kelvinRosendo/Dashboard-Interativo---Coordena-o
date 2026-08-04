package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.entity.UsuarioSegmento;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.repository.SegmentoRepository;
import br.com.escola.dashboard.repository.UsuarioRepository;
import br.com.escola.dashboard.repository.UsuarioSegmentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SegmentoRepository segmentoRepository;
    private final UsuarioSegmentoRepository usuarioSegmentoRepository;
    private final AdminAuthService adminAuthService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          SegmentoRepository segmentoRepository,
                          UsuarioSegmentoRepository usuarioSegmentoRepository,
                          AdminAuthService adminAuthService) {
        this.usuarioRepository = usuarioRepository;
        this.segmentoRepository = segmentoRepository;
        this.usuarioSegmentoRepository = usuarioSegmentoRepository;
        this.adminAuthService = adminAuthService;
    }

    @Transactional
    public Usuario buscarOuCriarPorGoogle(String googleId, String email, String nome, String fotoUrl) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> {
                    usuario.setNome(nome);
                    if (fotoUrl != null) {
                        usuario.setFotoUrl(fotoUrl);
                    }
                    if (googleId != null && usuario.getGoogleId() == null) {
                        usuario.setGoogleId(googleId);
                    }
                    return usuarioRepository.save(usuario);
                })
                .orElseGet(() -> {
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setGoogleId(googleId);
                    novoUsuario.setEmail(email);
                    novoUsuario.setNome(nome);
                    novoUsuario.setFotoUrl(fotoUrl);

                    if (adminAuthService.isAdminEmailAuthorized(email)) {
                        novoUsuario.setPerfil(PerfilUsuario.ADMIN);
                    } else {
                        novoUsuario.setPerfil(PerfilUsuario.COORDENADORA);
                    }

                    return usuarioRepository.save(novoUsuario);
                });
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorPerfil(PerfilUsuario perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarPerfil(Long usuarioId, PerfilUsuario novoPerfil) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));
        usuario.setPerfil(novoPerfil);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void adicionarSegmento(Long usuarioId, Long segmentoId) {
        if (usuarioSegmentoRepository.existsByUsuarioIdAndSegmentoId(usuarioId, segmentoId)) {
            return;
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));
        Segmento segmento = segmentoRepository.findById(segmentoId)
                .orElseThrow(() -> new IllegalArgumentException("Segmento nao encontrado: " + segmentoId));

        UsuarioSegmento us = new UsuarioSegmento();
        us.setUsuario(usuario);
        us.setSegmento(segmento);
        usuarioSegmentoRepository.save(us);
    }

    @Transactional
    public void removerSegmento(Long usuarioId, Long segmentoId) {
        usuarioSegmentoRepository.deleteByUsuarioIdAndSegmentoId(usuarioId, segmentoId);
    }

    public List<Segmento> buscarSegmentosDoUsuario(Long usuarioId) {
        List<Long> segmentoIds = usuarioSegmentoRepository.findSegmentoIdsByUsuarioId(usuarioId);
        return segmentoRepository.findAllById(segmentoIds);
    }

    public List<Segmento> buscarSegmentosDoUsuarioPorEmail(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) {
            return List.of();
        }
        return buscarSegmentosDoUsuario(usuario.getId());
    }

    public boolean isEmailCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}

package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.UsuarioRequestDTO;
import br.com.escola.dashboard.dto.UsuarioResponseDTO;
import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.entity.UsuarioSegmento;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.SegmentoRepository;
import br.com.escola.dashboard.repository.UsuarioRepository;
import br.com.escola.dashboard.repository.UsuarioSegmentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SegmentoRepository segmentoRepository;
    private final UsuarioSegmentoRepository usuarioSegmentoRepository;
    private final Set<String> adminEmails;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          SegmentoRepository segmentoRepository,
                          UsuarioSegmentoRepository usuarioSegmentoRepository,
                          @Value("${app.admin.authorized-emails:}") String adminEmailsConfig) {
        this.usuarioRepository = usuarioRepository;
        this.segmentoRepository = segmentoRepository;
        this.usuarioSegmentoRepository = usuarioSegmentoRepository;
        this.adminEmails = parseEmails(adminEmailsConfig);
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

                    if (isEmailAdmin(email)) {
                        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
                            usuario.setPerfil(PerfilUsuario.ADMIN);
                        }
                        if (usuario.getStatus() != StatusUsuario.ATIVO) {
                            usuario.setStatus(StatusUsuario.ATIVO);
                            usuario.setAtivo(true);
                        }
                    }

                    usuario.setUltimoLogin(LocalDateTime.now());
                    return usuarioRepository.save(usuario);
                })
                .orElseGet(() -> {
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setGoogleId(googleId);
                    novoUsuario.setEmail(email);
                    novoUsuario.setNome(nome);
                    novoUsuario.setFotoUrl(fotoUrl);
                    novoUsuario.setStatus(StatusUsuario.PENDENTE);

                    if (isEmailAdmin(email)) {
                        novoUsuario.setPerfil(PerfilUsuario.ADMIN);
                        novoUsuario.setStatus(StatusUsuario.ATIVO);
                    } else {
                        novoUsuario.setPerfil(PerfilUsuario.COORDENADORA);
                    }

                    novoUsuario.setUltimoLogin(LocalDateTime.now());
                    return usuarioRepository.save(novoUsuario);
                });
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorIdOuErro(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + id));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<Usuario> listarPorPerfil(PerfilUsuario perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }

    public List<Usuario> listarPorStatus(StatusUsuario status) {
        return usuarioRepository.findByStatusOrderByDataCriacaoDesc(status);
    }

    public List<Usuario> buscarComFiltros(String termo, PerfilUsuario perfil, StatusUsuario status) {
        return usuarioRepository.buscarComFiltros(termo, perfil, status);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = buscarPorIdOuErro(id);

        usuario.setNome(requestDTO.getNome());
        usuario.setPerfil(requestDTO.getPerfil());
        usuario.setStatus(requestDTO.getStatus());
        usuario.setAtivo(requestDTO.getStatus() != StatusUsuario.BLOQUEADO);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void aprovar(Long id, PerfilUsuario perfil, List<Long> segmentoIds) {
        Usuario usuario = buscarPorIdOuErro(id);

        usuario.setPerfil(perfil);
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        if (segmentoIds != null && !segmentoIds.isEmpty()) {
            for (Long segmentoId : segmentoIds) {
                adicionarSegmento(id, segmentoId);
            }
        }
    }

    @Transactional
    public void bloquear(Long id) {
        Usuario usuario = buscarPorIdOuErro(id);
        usuario.setStatus(StatusUsuario.BLOQUEADO);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desbloquear(Long id) {
        Usuario usuario = buscarPorIdOuErro(id);
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
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

    @Transactional
    public void sincronizarSegmentos(Long usuarioId, List<Long> novosSegmentoIds) {
        List<Segmento> atuais = buscarSegmentosDoUsuario(usuarioId);
        List<Long> idsAtuais = atuais.stream().map(Segmento::getId).toList();

        for (Long id : idsAtuais) {
            if (novosSegmentoIds == null || !novosSegmentoIds.contains(id)) {
                removerSegmento(usuarioId, id);
            }
        }

        if (novosSegmentoIds != null) {
            for (Long id : novosSegmentoIds) {
                if (!idsAtuais.contains(id)) {
                    adicionarSegmento(usuarioId, id);
                }
            }
        }
    }

    public List<Segmento> buscarSegmentosDoUsuario(Long usuarioId) {
        List<Long> segmentoIds = usuarioSegmentoRepository.findSegmentoIdsByUsuarioId(usuarioId);
        if (segmentoIds.isEmpty()) {
            return List.of();
        }
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

    public List<String> nomesSegmentosDoUsuario(Long usuarioId) {
        return buscarSegmentosDoUsuario(usuarioId).stream()
                .map(Segmento::getTitulo)
                .toList();
    }

    public Map<String, Long> contarPorPerfil() {
        return Map.of(
                "ADMIN", usuarioRepository.countByPerfil(PerfilUsuario.ADMIN),
                "VICE_DIRETORA", usuarioRepository.countByPerfil(PerfilUsuario.VICE_DIRETORA),
                "COORDENADORA", usuarioRepository.countByPerfil(PerfilUsuario.COORDENADORA)
        );
    }

    public Map<String, Long> contarPorStatus() {
        return Map.of(
                "ATIVO", usuarioRepository.countByStatus(StatusUsuario.ATIVO),
                "PENDENTE", usuarioRepository.countByStatus(StatusUsuario.PENDENTE),
                "BLOQUEADO", usuarioRepository.countByStatus(StatusUsuario.BLOQUEADO)
        );
    }

    public UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setFotoUrl(usuario.getFotoUrl());
        dto.setPerfil(usuario.getPerfil());
        dto.setStatus(usuario.getStatus());
        dto.setAtivo(usuario.getAtivo());
        dto.setUltimoLogin(usuario.getUltimoLogin());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setSegmentos(nomesSegmentosDoUsuario(usuario.getId()));
        return dto;
    }

    private boolean isEmailAdmin(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return adminEmails.contains(email.trim().toLowerCase());
    }

    private Set<String> parseEmails(String config) {
        if (config == null || config.isBlank()) {
            return Set.of();
        }
        return Set.of(Arrays.stream(config.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toArray(String[]::new));
    }
}

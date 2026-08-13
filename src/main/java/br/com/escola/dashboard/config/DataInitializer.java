package br.com.escola.dashboard.config;

import br.com.escola.dashboard.entity.*;
import br.com.escola.dashboard.enums.*;
import br.com.escola.dashboard.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Inicializador de dados estruturais.
 *
 * Dados temporais (semanas, cards, comunicados) sao inseridos pelas migrations Flyway (V14+).
 * Este componente cria apenas:
 * - Usuarios (necessario JPA para OAuth2)
 * - Vinculos usuario-segmento (necessario JPA)
 *
 * NENHUM dado com data hardcoded deve ser adicionado aqui.
 */
@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final SegmentoRepository segmentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSegmentoRepository usuarioSegmentoRepository;

    public DataInitializer(SegmentoRepository segmentoRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioSegmentoRepository usuarioSegmentoRepository) {
        this.segmentoRepository = segmentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioSegmentoRepository = usuarioSegmentoRepository;
    }

    @Override
    public void run(String... args) {
        popularUsuarios();
        popularSegmentosDosUsuarios();
    }

    private void popularUsuarios() {
        // VICE-DIRETORA
        criarOuAtualizarUsuario(
                "flaviaregina@colegiosatelite.com.br",
                "Flavia Regina",
                PerfilUsuario.VICE_DIRETORA,
                StatusUsuario.ATIVO
        );

        // COORDENADORAS
        criarOuAtualizarUsuario(
                "amanda.souza@colegiosatelite.com.br",
                "Amanda Cristina",
                PerfilUsuario.COORDENADORA,
                StatusUsuario.ATIVO
        );

        criarOuAtualizarUsuario(
                "edna.boniolo@colegiosatelite.com.br",
                "Edna Boniolo",
                PerfilUsuario.COORDENADORA,
                StatusUsuario.ATIVO
        );

        criarOuAtualizarUsuario(
                "elaine.bombarda@colegiosatelite.com.br",
                "Elaine Bombarda",
                PerfilUsuario.COORDENADORA,
                StatusUsuario.ATIVO
        );

        criarOuAtualizarUsuario(
                "ananda.caballero@colegiosatelite.com.br",
                "Ananda Caballero",
                PerfilUsuario.COORDENADORA,
                StatusUsuario.ATIVO
        );

        criarOuAtualizarUsuario(
                "lilian@colegiosatelite.com.br",
                "Lilian",
                PerfilUsuario.COORDENADORA,
                StatusUsuario.ATIVO
        );
    }

    private void criarOuAtualizarUsuario(String email, String nome, PerfilUsuario perfil, StatusUsuario status) {
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
        if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setPerfil(perfil);
            u.setStatus(status);
            u.setNome(nome);
            usuarioRepository.save(u);
        } else {
            Usuario u = new Usuario();
            u.setEmail(email);
            u.setNome(nome);
            u.setPerfil(perfil);
            u.setStatus(status);
            u.setAtivo(true);
            u.setDataCriacao(LocalDateTime.now());
            u.setDataAtualizacao(LocalDateTime.now());
            usuarioRepository.save(u);
        }
    }

    private void popularSegmentosDosUsuarios() {
        List<Segmento> todosSegmentos = segmentoRepository.findByAtivoTrueOrderByTitulo();
        if (todosSegmentos.isEmpty()) return;

        // Amanda - Fundamental 2 + Ensino Medio (conforme Data Pack)
        List<Segmento> amandaSegmentos = todosSegmentos.stream()
                .filter(s -> s.getSlug().equals("fundamental-2") || s.getSlug().equals("ensino-medio"))
                .toList();
        vincularSegmentos("amanda.souza@colegiosatelite.com.br", amandaSegmentos);

        // Edna - Fundamental 2 + Ensino Medio (conforme Data Pack)
        List<Segmento> ednaSegmentos = todosSegmentos.stream()
                .filter(s -> s.getSlug().equals("fundamental-2") || s.getSlug().equals("ensino-medio"))
                .toList();
        vincularSegmentos("edna.boniolo@colegiosatelite.com.br", ednaSegmentos);

        // Elaine - Educacao Infantil + Fundamental 1 (conforme Data Pack)
        List<Segmento> elaineSegmentos = todosSegmentos.stream()
                .filter(s -> s.getSlug().equals("educacao-infantil") || s.getSlug().equals("fundamental-1"))
                .toList();
        vincularSegmentos("elaine.bombarda@colegiosatelite.com.br", elaineSegmentos);

        // Ananda - Fundamental 2 + Ensino Medio (conforme Data Pack)
        List<Segmento> anandaSegmentos = todosSegmentos.stream()
                .filter(s -> s.getSlug().equals("fundamental-2") || s.getSlug().equals("ensino-medio"))
                .toList();
        vincularSegmentos("ananda.caballero@colegiosatelite.com.br", anandaSegmentos);

        // Lilian - Fundamental 2 + Ensino Medio (conforme Data Pack)
        List<Segmento> lilianSegmentos = todosSegmentos.stream()
                .filter(s -> s.getSlug().equals("fundamental-2") || s.getSlug().equals("ensino-medio"))
                .toList();
        vincularSegmentos("lilian@colegiosatelite.com.br", lilianSegmentos);

        // Flavia (Vice-Diretora) - todos os segmentos
        vincularSegmentos("flaviaregina@colegiosatelite.com.br", todosSegmentos);
    }

    private void vincularSegmentos(String email, List<Segmento> segmentos) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) return;

        Usuario usuario = usuarioOpt.get();
        for (Segmento seg : segmentos) {
            if (!usuarioSegmentoRepository.existsByUsuarioIdAndSegmentoId(usuario.getId(), seg.getId())) {
                UsuarioSegmento us = new UsuarioSegmento();
                us.setUsuario(usuario);
                us.setSegmento(seg);
                us.setDataCriacao(LocalDateTime.now());
                usuarioSegmentoRepository.save(us);
            }
        }
    }
}

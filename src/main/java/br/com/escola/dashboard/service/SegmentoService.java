package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.repository.SegmentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SegmentoService {

    private final SegmentoRepository segmentoRepository;

    public SegmentoService(SegmentoRepository segmentoRepository) {
        this.segmentoRepository = segmentoRepository;
    }

    public List<Segmento> listarTodos() {
        return segmentoRepository.findAll();
    }

    public List<Segmento> listarAtivos() {
        return segmentoRepository.findByAtivoTrueOrderByTitulo();
    }

    public Optional<Segmento> buscarPorSlug(String slug) {
        return segmentoRepository.findBySlug(slug);
    }

    public Optional<Segmento> buscarPorId(Long id) {
        return segmentoRepository.findById(id);
    }
}

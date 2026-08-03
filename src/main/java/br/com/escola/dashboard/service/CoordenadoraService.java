package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Coordenadora;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.CoordenadoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordenadoraService {

    private final CoordenadoraRepository coordenadoraRepository;

    public CoordenadoraService(CoordenadoraRepository coordenadoraRepository) {
        this.coordenadoraRepository = coordenadoraRepository;
    }

    public List<Coordenadora> listarTodas() {
        return coordenadoraRepository.findAll();
    }

    public List<Coordenadora> listarPorSegmento(SegmentoCoordenacao segmento) {
        return coordenadoraRepository.findBySegmento(segmento);
    }

    public Optional<Coordenadora> buscarPorId(Long id) {
        return coordenadoraRepository.findById(id);
    }

    public Optional<Coordenadora> buscarPorEmail(String email) {
        return coordenadoraRepository.findByEmail(email);
    }

    public Coordenadora salvar(Coordenadora coordenadora) {
        return coordenadoraRepository.save(coordenadora);
    }

    public void excluir(Long id) {
        coordenadoraRepository.deleteById(id);
    }

    public boolean existeEmail(String email) {
        return coordenadoraRepository.existsByEmail(email);
    }
}

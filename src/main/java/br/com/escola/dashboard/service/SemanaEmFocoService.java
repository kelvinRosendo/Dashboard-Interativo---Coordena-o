package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SemanaEmFocoService {

    private final SemanaEmFocoRepository repository;

    public SemanaEmFocoService(SemanaEmFocoRepository repository) {
        this.repository = repository;
    }

    public Optional<SemanaEmFoco> buscarAtiva() {
        return repository.findByAtivaTrueOrderByAtualizadoEmDesc().stream().findFirst();
    }

    public Optional<SemanaEmFoco> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    public List<SemanaEmFoco> listarTodas() {
        return repository.findAll();
    }

    public List<SemanaEmFoco> listarAtivasPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return repository.findByAtivaTrueAndSegmentoIn(segmentos);
    }

    public Optional<SemanaEmFoco> buscarAtivaPorSegmento(SegmentoCoordenacao segmento) {
        if (segmento == null) {
            return Optional.empty();
        }
        return repository.findBySegmento(segmento).stream()
                .filter(SemanaEmFoco::isAtiva)
                .findFirst();
    }

    public List<SemanaEmFoco> listarPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return repository.findBySegmentoIn(segmentos);
    }

    @Transactional
    public SemanaEmFoco salvar(SemanaEmFoco semana) {
        if (semana.isAtiva()) {
            List<SemanaEmFoco> ativas = repository.findByAtivaTrueOrderByAtualizadoEmDesc();
            for (SemanaEmFoco ativa : ativas) {
                if (semana.getId() == null || !ativa.getId().equals(semana.getId())) {
                    ativa.setAtiva(false);
                    repository.save(ativa);
                }
            }
        }
        return repository.save(semana);
    }
}

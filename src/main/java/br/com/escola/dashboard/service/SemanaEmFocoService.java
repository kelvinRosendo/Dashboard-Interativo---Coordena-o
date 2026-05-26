package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.SemanaEmFoco;
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

    @Transactional
    public SemanaEmFoco salvar(SemanaEmFoco semana) {
        if (semana.isAtiva()) {
            List<SemanaEmFoco> ativas = repository.findAll().stream()
                    .filter(SemanaEmFoco::isAtiva)
                    .toList();
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

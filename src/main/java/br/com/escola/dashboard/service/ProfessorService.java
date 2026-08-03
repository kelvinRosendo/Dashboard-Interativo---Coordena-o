package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Professor;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public List<Professor> listarPorSegmento(SegmentoCoordenacao segmento) {
        return professorRepository.findBySegmento(segmento);
    }

    public Optional<Professor> buscarPorId(Long id) {
        return professorRepository.findById(id);
    }

    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    public void excluir(Long id) {
        professorRepository.deleteById(id);
    }

    public boolean existeEmail(String email) {
        return professorRepository.existsByEmail(email);
    }
}

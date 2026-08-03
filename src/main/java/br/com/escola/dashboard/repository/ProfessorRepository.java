package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Professor;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    List<Professor> findBySegmento(SegmentoCoordenacao segmento);

    List<Professor> findByDisciplina(String disciplina);

    boolean existsByEmail(String email);
}

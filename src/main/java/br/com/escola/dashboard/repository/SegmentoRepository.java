package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Segmento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentoRepository extends JpaRepository<Segmento, Long> {

    Optional<Segmento> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Segmento> findByAtivoTrue();

    List<Segmento> findByAtivoTrueOrderByTitulo();
}

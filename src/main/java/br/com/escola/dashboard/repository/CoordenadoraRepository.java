package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Coordenadora;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoordenadoraRepository extends JpaRepository<Coordenadora, Long> {

    List<Coordenadora> findBySegmento(SegmentoCoordenacao segmento);

    Optional<Coordenadora> findByEmail(String email);

    boolean existsByEmail(String email);
}

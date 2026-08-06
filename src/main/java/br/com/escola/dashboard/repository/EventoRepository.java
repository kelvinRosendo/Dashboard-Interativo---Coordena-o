package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Evento;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByDataInicioBetweenOrderByDataInicioAsc(LocalDate inicio, LocalDate fim);

    Optional<Evento> findByGoogleEventId(String googleEventId);

    boolean existsByGoogleEventId(String googleEventId);

    List<Evento> findBySegmentoInOrderByDataInicioAsc(List<SegmentoCoordenacao> segmentos);

    List<Evento> findByDataInicioBetweenAndSegmentoInOrderByDataInicioAsc(LocalDate inicio, LocalDate fim, List<SegmentoCoordenacao> segmentos);

    List<Evento> findBySegmentoIsNullOrderByDataInicioAsc();
}

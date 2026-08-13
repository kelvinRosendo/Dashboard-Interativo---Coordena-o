package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemanaEmFocoRepository extends JpaRepository<SemanaEmFoco, Long> {

    @EntityGraph(attributePaths = {"relatorio"})
    Optional<SemanaEmFoco> findByAtivaTrue();

    @EntityGraph(attributePaths = {"relatorio"})
    List<SemanaEmFoco> findByAtivaTrueOrderByAtualizadoEmDesc();

    List<SemanaEmFoco> findBySegmento(SegmentoCoordenacao segmento);

    @EntityGraph(attributePaths = {"relatorio"})
    List<SemanaEmFoco> findByAtivaTrueAndSegmentoIn(List<SegmentoCoordenacao> segmentos);

    @EntityGraph(attributePaths = {"relatorio"})
    List<SemanaEmFoco> findBySegmentoIn(List<SegmentoCoordenacao> segmentos);

    @Query("SELECT COUNT(s) FROM SemanaEmFoco s WHERE s.relatorio IS NULL AND s.ativa = true")
    long countAtivasSemRelatorio();

    @Query("SELECT COUNT(s) FROM SemanaEmFoco s WHERE s.relatorio IS NULL AND s.ativa = true AND s.segmento IN :segmentos")
    long countAtivasSemRelatorioPorSegmentos(@Param("segmentos") List<SegmentoCoordenacao> segmentos);
}

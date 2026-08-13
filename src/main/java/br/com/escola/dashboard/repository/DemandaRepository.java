package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandaRepository extends JpaRepository<Demanda, Long> {

    List<Demanda> findAllByOrderByDataPrazoAscDataCriacaoDesc();

    List<Demanda> findBySegmentoOrderByDataPrazoAscDataCriacaoDesc(SegmentoCoordenacao segmento);

    List<Demanda> findByStatusOrderByDataPrazoAscDataCriacaoDesc(StatusDemanda status);

    List<Demanda> findBySegmentoAndStatusOrderByDataPrazoAscDataCriacaoDesc(SegmentoCoordenacao segmento,
                                                                             StatusDemanda status);

    List<Demanda> findBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalseOrderByDataPrazoAscDataCriacaoDesc(
            SegmentoCoordenacao segmento,
            StatusDemanda status
    );

    List<Demanda> findByDataPrazoOrderByTituloAsc(LocalDate dataPrazo);

    long countBySegmento(SegmentoCoordenacao segmento);

    long countByStatus(StatusDemanda status);

    long countBySegmentoAndStatus(SegmentoCoordenacao segmento, StatusDemanda status);

    long countBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalse(SegmentoCoordenacao segmento,
                                                                      StatusDemanda status);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.segmento = :segmento AND d.status IN :statuses")
    long countBySegmentoAndStatusIn(@Param("segmento") SegmentoCoordenacao segmento,
                                     @Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT d FROM Demanda d WHERE d.status IN :statuses ORDER BY d.dataPrazo ASC, d.dataCriacao DESC")
    List<Demanda> findByStatusInOrderByDataPrazoAscDataCriacaoDesc(@Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT d FROM Demanda d WHERE d.segmento = :segmento AND d.status IN :statuses ORDER BY d.dataPrazo ASC, d.dataCriacao DESC")
    List<Demanda> findBySegmentoAndStatusInOrderByDataPrazoAscDataCriacaoDesc(
            @Param("segmento") SegmentoCoordenacao segmento,
            @Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.dataPrazo IS NOT NULL AND d.dataPrazo >= :inicio AND d.dataPrazo <= :fim AND d.status NOT IN :statusExcluidos")
    long countByDataPrazoBetweenAndStatusNotIn(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("statusExcluidos") List<StatusDemanda> statusExcluidos);

    @Query("SELECT d FROM Demanda d WHERE d.segmento IN :segmentos AND d.status IN :statuses ORDER BY d.dataPrazo ASC, d.dataCriacao DESC")
    List<Demanda> findBySegmentoInAndStatusInOrderByDataPrazoAscDataCriacaoDesc(
            @Param("segmentos") List<SegmentoCoordenacao> segmentos,
            @Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT d FROM Demanda d WHERE d.segmento IN :segmentos ORDER BY d.dataPrazo ASC, d.dataCriacao DESC")
    List<Demanda> findBySegmentoInOrderByDataPrazoAscDataCriacaoDesc(
            @Param("segmentos") List<SegmentoCoordenacao> segmentos);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.segmento IN :segmentos AND d.status IN :statuses")
    long countBySegmentoInAndStatusIn(
            @Param("segmentos") List<SegmentoCoordenacao> segmentos,
            @Param("statuses") List<StatusDemanda> statuses);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.segmento IN :segmentos AND d.status = :status")
    long countBySegmentoInAndStatus(
            @Param("segmentos") List<SegmentoCoordenacao> segmentos,
            @Param("status") StatusDemanda status);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.segmento IN :segmentos")
    long countBySegmentoIn(@Param("segmentos") List<SegmentoCoordenacao> segmentos);

    @Query("SELECT COUNT(d) FROM Demanda d WHERE d.segmento IN :segmentos AND d.dataPrazo IS NOT NULL AND d.dataPrazo >= :inicio AND d.dataPrazo <= :fim AND d.status NOT IN :statusExcluidos")
    long countBySegmentoInAndDataPrazoBetweenAndStatusNotIn(
            @Param("segmentos") List<SegmentoCoordenacao> segmentos,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("statusExcluidos") List<StatusDemanda> statusExcluidos);
}

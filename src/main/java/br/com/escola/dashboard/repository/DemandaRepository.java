package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandaRepository extends JpaRepository<Demanda, Long> {

    List<Demanda> findAllByOrderByDataPrazoAscDataCriacaoDesc();

    List<Demanda> findBySegmentoOrderByDataPrazoAscDataCriacaoDesc(SegmentoCoordenacao segmento);

    List<Demanda> findByStatusOrderByDataPrazoAscDataCriacaoDesc(StatusDemanda status);

    List<Demanda> findBySegmentoAndStatusOrderByDataPrazoAscDataCriacaoDesc(SegmentoCoordenacao segmento,
                                                                             StatusDemanda status);

    long countBySegmento(SegmentoCoordenacao segmento);

    long countByStatus(StatusDemanda status);

    long countBySegmentoAndStatus(SegmentoCoordenacao segmento, StatusDemanda status);
}

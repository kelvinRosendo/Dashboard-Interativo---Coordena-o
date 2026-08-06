package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Aviso;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    List<Aviso> findBySegmento(SegmentoCoordenacao segmento);

    List<Aviso> findByOrderByDataCriacaoDesc();

    List<Aviso> findBySegmentoInOrderByDataCriacaoDesc(List<SegmentoCoordenacao> segmentos);

    List<Aviso> findBySegmentoIsNullOrderByDataCriacaoDesc();
}

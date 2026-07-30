package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.comunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunicadoRepository extends JpaRepository<comunicado, Long> {

    List<comunicado> findAllByOrderByDataCriacaoDesc();
}

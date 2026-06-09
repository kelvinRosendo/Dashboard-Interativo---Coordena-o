package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.RelatorioSemanaEmFoco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelatorioSemanaEmFocoRepository extends JpaRepository<RelatorioSemanaEmFoco, Long> {

    Optional<RelatorioSemanaEmFoco> findBySemanaEmFocoId(Long semanaId);

    List<RelatorioSemanaEmFoco> findByCoordenadoraIdOrderByCriadoEmDesc(String coordenadoraId);

    List<RelatorioSemanaEmFoco> findAllByOrderByCriadoEmDesc();
}

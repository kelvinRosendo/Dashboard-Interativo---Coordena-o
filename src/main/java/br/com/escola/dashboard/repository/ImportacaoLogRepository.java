package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.ImportacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportacaoLogRepository extends JpaRepository<ImportacaoLog, Long> {

    Optional<ImportacaoLog> findTopByOrderByDataImportacaoDesc();

    List<ImportacaoLog> findAllByOrderByDataImportacaoDesc();

    List<ImportacaoLog> findTop10ByOrderByDataImportacaoDesc();
}

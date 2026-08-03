package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.ImportacaoLog;
import br.com.escola.dashboard.repository.ImportacaoLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImportacaoLogService {

    private final ImportacaoLogRepository importacaoLogRepository;

    public ImportacaoLogService(ImportacaoLogRepository importacaoLogRepository) {
        this.importacaoLogRepository = importacaoLogRepository;
    }

    public ImportacaoLog salvar(ImportacaoLog log) {
        return importacaoLogRepository.save(log);
    }

    public Optional<ImportacaoLog> buscarUltima() {
        return importacaoLogRepository.findTopByOrderByDataImportacaoDesc();
    }

    public List<ImportacaoLog> listarHistorico() {
        return importacaoLogRepository.findAllByOrderByDataImportacaoDesc();
    }

    public List<ImportacaoLog> listarRecentes() {
        return importacaoLogRepository.findTop10ByOrderByDataImportacaoDesc();
    }
}

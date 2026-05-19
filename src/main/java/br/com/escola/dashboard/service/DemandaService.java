package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.DemandaRequestDTO;
import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;

    public DemandaService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public Demanda criarDemanda(DemandaRequestDTO requestDTO, String criadaPor) {
        Demanda demanda = new Demanda();
        demanda.setTitulo(requestDTO.getTitulo().trim());
        demanda.setDescricao(limparTexto(requestDTO.getDescricao()));
        demanda.setSegmento(requestDTO.getSegmento());
        demanda.setPrioridade(requestDTO.getPrioridade());
        demanda.setStatus(StatusDemanda.PENDENTE);
        demanda.setDataCriacao(LocalDateTime.now());
        demanda.setDataPrazo(requestDTO.getDataPrazo());
        demanda.setCriadaPor(limparTexto(criadaPor));

        return demandaRepository.save(demanda);
    }

    public List<Demanda> listarTodasParaAdmin() {
        return demandaRepository.findAllByOrderByDataPrazoAscDataCriacaoDesc();
    }

    public List<Demanda> listarPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.findBySegmentoOrderByDataPrazoAscDataCriacaoDesc(segmento);
    }

    public Demanda atualizarStatus(Long id, StatusDemanda status) {
        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demanda nao encontrada com id: " + id));

        demanda.setStatus(status);
        return demandaRepository.save(demanda);
    }

    public ResumoDemandas resumoGeral() {
        long total = demandaRepository.count();
        long pendentes = demandaRepository.countByStatus(StatusDemanda.PENDENTE);
        long emAndamento = demandaRepository.countByStatus(StatusDemanda.EM_ANDAMENTO);
        long concluidas = demandaRepository.countByStatus(StatusDemanda.CONCLUIDA);
        long canceladas = demandaRepository.countByStatus(StatusDemanda.CANCELADA);
        long proximasDoPrazo = contarProximasDoPrazo();

        List<ProgressoSegmento> progressos = Arrays.stream(SegmentoCoordenacao.values())
                .map(this::calcularProgressoPorSegmento)
                .toList();

        return new ResumoDemandas(total, pendentes, emAndamento, concluidas, canceladas, proximasDoPrazo, progressos);
    }

    public ProgressoSegmento calcularProgressoPorSegmento(SegmentoCoordenacao segmento) {
        long total = demandaRepository.countBySegmento(segmento);
        long pendentes = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE);
        long emAndamento = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.EM_ANDAMENTO);
        long concluidas = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CONCLUIDA);
        long canceladas = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CANCELADA);
        int percentual = total > 0 ? (int) Math.round((concluidas * 100.0) / total) : 0;

        return new ProgressoSegmento(
                segmento,
                segmento.getSlug(),
                segmento.getTitulo(),
                total,
                pendentes,
                emAndamento,
                concluidas,
                canceladas,
                percentual
        );
    }

    public long contarPendentesPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE);
    }

    private long contarProximasDoPrazo() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        return demandaRepository.findAll().stream()
                .filter(demanda -> demanda.getDataPrazo() != null)
                .filter(demanda -> !demanda.getDataPrazo().isBefore(hoje))
                .filter(demanda -> !demanda.getDataPrazo().isAfter(limite))
                .filter(demanda -> demanda.getStatus() != StatusDemanda.CONCLUIDA)
                .filter(demanda -> demanda.getStatus() != StatusDemanda.CANCELADA)
                .count();
    }

    private String limparTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }

    public record ResumoDemandas(
            long total,
            long pendentes,
            long emAndamento,
            long concluidas,
            long canceladas,
            long proximasDoPrazo,
            List<ProgressoSegmento> progressos
    ) {
    }

    public record ProgressoSegmento(
            SegmentoCoordenacao segmento,
            String slug,
            String titulo,
            long total,
            long pendentes,
            long emAndamento,
            long concluidas,
            long canceladas,
            int percentual
    ) {
    }
}

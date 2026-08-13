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

    public static final List<StatusDemanda> STATUS_ATIVOS = List.of(
            StatusDemanda.PENDENTE,
            StatusDemanda.EM_ANDAMENTO
    );

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

    public List<Demanda> listarAtivas() {
        return demandaRepository.findByStatusInOrderByDataPrazoAscDataCriacaoDesc(STATUS_ATIVOS);
    }

    public List<Demanda> listarAtivasPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.findBySegmentoAndStatusInOrderByDataPrazoAscDataCriacaoDesc(segmento, STATUS_ATIVOS);
    }

    public Demanda atualizarStatus(Long id, StatusDemanda status) {
        Demanda demanda = buscarDemanda(id);
        demanda.setStatus(status);
        return demandaRepository.save(demanda);
    }

    public Demanda atualizarStatusParaSegmento(Long id, StatusDemanda status, SegmentoCoordenacao segmento) {
        Demanda demanda = buscarDemanda(id);

        if (demanda.getSegmento() != segmento) {
            throw new IllegalArgumentException("Demanda nao pertence ao segmento informado.");
        }

        demanda.setStatus(status);
        demanda.setVisualizadaPelaCoordenadora(true);
        return demandaRepository.save(demanda);
    }

    private Demanda buscarDemanda(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID nao pode ser nulo");
        }
        return demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demanda nao encontrada com id: " + id));
    }

    public ResumoDemandas resumoGeral() {
        long total = demandaRepository.count();
        long pendentes = demandaRepository.countByStatus(StatusDemanda.PENDENTE);
        long emAndamento = demandaRepository.countByStatus(StatusDemanda.EM_ANDAMENTO);
        long concluidas = demandaRepository.countByStatus(StatusDemanda.CONCLUIDA);
        long canceladas = demandaRepository.countByStatus(StatusDemanda.CANCELADA);
        long ativas = demandaRepository.countByStatusIn(STATUS_ATIVOS);
        long proximasDoPrazo = contarProximasDoPrazo();

        List<ProgressoSegmento> progressos = Arrays.stream(SegmentoCoordenacao.values())
                .map(this::calcularProgressoPorSegmento)
                .toList();

        return new ResumoDemandas(total, pendentes, emAndamento, concluidas, canceladas, ativas, proximasDoPrazo, progressos);
    }

    public ProgressoSegmento calcularProgressoPorSegmento(SegmentoCoordenacao segmento) {
        long total = demandaRepository.countBySegmento(segmento);
        long pendentes = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE);
        long emAndamento = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.EM_ANDAMENTO);
        long concluidas = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CONCLUIDA);
        long canceladas = demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.CANCELADA);
        long ativas = demandaRepository.countBySegmentoAndStatusIn(segmento, STATUS_ATIVOS);
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
                ativas,
                percentual
        );
    }

    public long contarPendentesPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.countBySegmentoAndStatus(segmento, StatusDemanda.PENDENTE);
    }

    public List<Demanda> listarNovasPendentesPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.findBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalseOrderByDataPrazoAscDataCriacaoDesc(
                segmento,
                StatusDemanda.PENDENTE
        );
    }

    public long contarNovasPendentesPorSegmento(SegmentoCoordenacao segmento) {
        return demandaRepository.countBySegmentoAndStatusAndVisualizadaPelaCoordenadoraFalse(
                segmento,
                StatusDemanda.PENDENTE
        );
    }

    public void marcarNovasPendentesComoVisualizadas(SegmentoCoordenacao segmento) {
        List<Demanda> demandasNovas = listarNovasPendentesPorSegmento(segmento);

        demandasNovas.forEach(demanda -> demanda.setVisualizadaPelaCoordenadora(true));
        demandaRepository.saveAll(demandasNovas);
    }

    public List<Demanda> listarAtivasPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return demandaRepository.findBySegmentoInAndStatusInOrderByDataPrazoAscDataCriacaoDesc(segmentos, STATUS_ATIVOS);
    }

    public List<Demanda> listarPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return demandaRepository.findBySegmentoInOrderByDataPrazoAscDataCriacaoDesc(segmentos);
    }

    public ResumoDemandas resumoPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return new ResumoDemandas(0, 0, 0, 0, 0, 0, 0, List.of());
        }

        long total = demandaRepository.countBySegmentoIn(segmentos);
        long pendentes = demandaRepository.countBySegmentoInAndStatus(segmentos, StatusDemanda.PENDENTE);
        long emAndamento = demandaRepository.countBySegmentoInAndStatus(segmentos, StatusDemanda.EM_ANDAMENTO);
        long concluidas = demandaRepository.countBySegmentoInAndStatus(segmentos, StatusDemanda.CONCLUIDA);
        long canceladas = demandaRepository.countBySegmentoInAndStatus(segmentos, StatusDemanda.CANCELADA);
        long ativas = demandaRepository.countBySegmentoInAndStatusIn(segmentos, STATUS_ATIVOS);
        long proximasDoPrazo = contarProximasDoPrazoPorSegmentos(segmentos);

        List<ProgressoSegmento> progressos = segmentos.stream()
                .map(this::calcularProgressoPorSegmento)
                .toList();

        return new ResumoDemandas(total, pendentes, emAndamento, concluidas, canceladas, ativas, proximasDoPrazo, progressos);
    }

    private long contarProximasDoPrazoPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        return demandaRepository.countBySegmentoInAndDataPrazoBetweenAndStatusNotIn(
                segmentos,
                hoje, limite,
                List.of(StatusDemanda.CONCLUIDA, StatusDemanda.CANCELADA)
        );
    }

    private long contarProximasDoPrazo() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        return demandaRepository.countByDataPrazoBetweenAndStatusNotIn(
                hoje, limite,
                List.of(StatusDemanda.CONCLUIDA, StatusDemanda.CANCELADA)
        );
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
            long ativas,
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
            long ativas,
            int percentual
    ) {
    }
}

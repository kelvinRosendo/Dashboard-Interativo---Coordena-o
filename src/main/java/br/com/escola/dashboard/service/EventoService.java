package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Evento;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return eventoRepository.findByDataInicioBetweenOrderByDataInicioAsc(inicio, fim);
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public Optional<Evento> buscarPorGoogleEventId(String googleEventId) {
        return eventoRepository.findByGoogleEventId(googleEventId);
    }

    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void excluir(Long id) {
        eventoRepository.deleteById(id);
    }

    public boolean existeGoogleEventId(String googleEventId) {
        return eventoRepository.existsByGoogleEventId(googleEventId);
    }

    public List<Evento> listarPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return eventoRepository.findBySegmentoInOrderByDataInicioAsc(segmentos);
    }

    public List<Evento> listarPorPeriodoESegmentos(LocalDate inicio, LocalDate fim, List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return eventoRepository.findByDataInicioBetweenAndSegmentoInOrderByDataInicioAsc(inicio, fim, segmentos);
    }

    public List<Evento> listarCompartilhados() {
        return eventoRepository.findBySegmentoIsNullOrderByDataInicioAsc();
    }

    public List<Evento> listarPorSegmentosECompartilhados(List<SegmentoCoordenacao> segmentos) {
        List<Evento> dosSegmentos = listarPorSegmentos(segmentos);
        List<Evento> compartilhados = listarCompartilhados();
        List<Evento> resultado = new java.util.ArrayList<>(dosSegmentos);
        resultado.addAll(compartilhados);
        return resultado;
    }
}

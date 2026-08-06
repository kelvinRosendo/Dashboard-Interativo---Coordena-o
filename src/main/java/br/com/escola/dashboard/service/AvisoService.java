package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Aviso;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.repository.AvisoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;

    public AvisoService(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    public List<Aviso> listarTodos() {
        return avisoRepository.findByOrderByDataCriacaoDesc();
    }

    public List<Aviso> listarPorSegmento(SegmentoCoordenacao segmento) {
        return avisoRepository.findBySegmento(segmento);
    }

    public Optional<Aviso> buscarPorId(Long id) {
        return avisoRepository.findById(id);
    }

    public Aviso salvar(Aviso aviso) {
        return avisoRepository.save(aviso);
    }

    public void excluir(Long id) {
        avisoRepository.deleteById(id);
    }

    public List<Aviso> listarPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        if (segmentos == null || segmentos.isEmpty()) {
            return List.of();
        }
        return avisoRepository.findBySegmentoInOrderByDataCriacaoDesc(segmentos);
    }

    public List<Aviso> listarGlobais() {
        return avisoRepository.findBySegmentoIsNullOrderByDataCriacaoDesc();
    }

    public List<Aviso> listarGlobaisEPorSegmentos(List<SegmentoCoordenacao> segmentos) {
        List<Aviso> dosSegmentos = listarPorSegmentos(segmentos);
        List<Aviso> globais = listarGlobais();
        List<Aviso> resultado = new ArrayList<>(globais);
        resultado.addAll(dosSegmentos);
        return resultado;
    }
}

package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.RelatorioSemanaEmFocoDTO;
import br.com.escola.dashboard.entity.RelatorioSemanaEmFoco;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.enums.StatusRelatorio;
import br.com.escola.dashboard.repository.RelatorioSemanaEmFocoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioSemanaEmFocoService {

    private final RelatorioSemanaEmFocoRepository repository;
    private final SemanaEmFocoService semanaEmFocoService;

    public RelatorioSemanaEmFocoService(RelatorioSemanaEmFocoRepository repository,
                                        SemanaEmFocoService semanaEmFocoService) {
        this.repository = repository;
        this.semanaEmFocoService = semanaEmFocoService;
    }

    public Optional<RelatorioSemanaEmFoco> obterPorSemanaId(Long semanaId) {
        return repository.findBySemanaEmFocoId(semanaId);
    }

    public Optional<RelatorioSemanaEmFoco> obterPorId(Long relatorioId) {
        return repository.findById(relatorioId);
    }

    public List<RelatorioSemanaEmFoco> obterTodos() {
        return repository.findAllByOrderByCriadoEmDesc();
    }

    public List<RelatorioSemanaEmFoco> obterPorCoordenadora(String coordenadoraId) {
        return repository.findByCoordenadoraIdOrderByCriadoEmDesc(coordenadoraId);
    }

    @Transactional
    public RelatorioSemanaEmFoco criarOuObter(SemanaEmFoco semana, String coordenadoraId,
                                              String coordenadoraNome, String coordenadoraEmail) {
        Optional<RelatorioSemanaEmFoco> existente = repository.findBySemanaEmFocoId(semana.getId());

        if (existente.isPresent()) {
            return existente.get();
        }

        RelatorioSemanaEmFoco novo = new RelatorioSemanaEmFoco();
        novo.setSemanaEmFoco(semana);
        novo.setCoordenadoraId(coordenadoraId);
        novo.setCoordenadoraNome(coordenadoraNome);
        novo.setCoordenadoraEmail(coordenadoraEmail);
        novo.setDataInicio(semana.getDataInicio());
        novo.setDataFim(semana.getDataFim());
        novo.setStatus(StatusRelatorio.RASCUNHO);

        return repository.save(novo);
    }

    @Transactional
    public RelatorioSemanaEmFoco atualizar(Long relatorioId, RelatorioSemanaEmFocoDTO dto,
                                           String coordenadoraId) {
        RelatorioSemanaEmFoco relatorio = repository.findById(relatorioId)
                .orElseThrow(() -> new IllegalArgumentException("Relatorio nao encontrado"));

        validarPropriedade(relatorio, coordenadoraId);
        validarEdicao(relatorio);

        relatorio.setResumoSemana(dto.getResumoSemana());
        relatorio.setAtividadesExecutadas(dto.getAtividadesExecutadas());
        relatorio.setPendencias(dto.getPendencias());
        relatorio.setObservacoes(dto.getObservacoes());
        relatorio.setConclusao(dto.getConclusao());

        return repository.save(relatorio);
    }

    @Transactional
    public RelatorioSemanaEmFoco finalizar(Long relatorioId, String coordenadoraId,
                                           String finalizadoPor) {
        RelatorioSemanaEmFoco relatorio = repository.findById(relatorioId)
                .orElseThrow(() -> new IllegalArgumentException("Relatorio nao encontrado"));

        validarPropriedade(relatorio, coordenadoraId);

        if (relatorio.getStatus() == StatusRelatorio.FINALIZADO) {
            throw new IllegalStateException("Relatorio ja foi finalizado");
        }

        relatorio.setStatus(StatusRelatorio.FINALIZADO);
        relatorio.setFinalizadoEm(LocalDateTime.now());
        relatorio.setFinalizadoPor(finalizadoPor);

        return repository.save(relatorio);
    }

    private void validarPropriedade(RelatorioSemanaEmFoco relatorio, String coordenadoraId) {
        if (!relatorio.getCoordenadoraId().equals(coordenadoraId)) {
            throw new SecurityException("Voce nao tem permissao para acessar este relatorio");
        }
    }

    private void validarEdicao(RelatorioSemanaEmFoco relatorio) {
        if (relatorio.getStatus() == StatusRelatorio.FINALIZADO) {
            throw new IllegalStateException("Nao e possivel editar um relatorio finalizado");
        }
    }

    public RelatorioSemanaEmFocoDTO converterParaDTO(RelatorioSemanaEmFoco relatorio) {
        RelatorioSemanaEmFocoDTO dto = new RelatorioSemanaEmFocoDTO();
        dto.setId(relatorio.getId());
        dto.setSemanaEmFocoId(relatorio.getSemanaEmFoco().getId());
        dto.setCoordenadoraId(relatorio.getCoordenadoraId());
        dto.setCoordenadoraNome(relatorio.getCoordenadoraNome());
        dto.setCoordenadoraEmail(relatorio.getCoordenadoraEmail());
        dto.setResumoSemana(relatorio.getResumoSemana());
        dto.setAtividadesExecutadas(relatorio.getAtividadesExecutadas());
        dto.setPendencias(relatorio.getPendencias());
        dto.setObservacoes(relatorio.getObservacoes());
        dto.setConclusao(relatorio.getConclusao());
        dto.setStatus(relatorio.getStatus());
        dto.setCriadoEm(relatorio.getCriadoEm());
        dto.setAtualizadoEm(relatorio.getAtualizadoEm());
        dto.setFinalizadoEm(relatorio.getFinalizadoEm());
        dto.setFinalizadoPor(relatorio.getFinalizadoPor());
        return dto;
    }
}

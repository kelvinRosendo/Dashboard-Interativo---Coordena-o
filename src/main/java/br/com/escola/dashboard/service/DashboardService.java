package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.*;
import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.mapper.DashboardMapper;
import br.com.escola.dashboard.repository.ImportacaoLogRepository;
import br.com.escola.dashboard.repository.SemanaEmFocoRepository;
import br.com.escola.dashboard.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final ComunicadoService comunicadoService;
    private final AvisoService avisoService;
    private final EventoService eventoService;
    private final UsuarioRepository usuarioRepository;
    private final ImportacaoLogRepository importacaoLogRepository;
    private final SemanaEmFocoRepository semanaEmFocoRepository;

    public DashboardService(PerfilService perfilService,
                            UsuarioService usuarioService,
                            DemandaService demandaService,
                            SemanaEmFocoService semanaEmFocoService,
                            ComunicadoService comunicadoService,
                            AvisoService avisoService,
                            EventoService eventoService,
                            UsuarioRepository usuarioRepository,
                            ImportacaoLogRepository importacaoLogRepository,
                            SemanaEmFocoRepository semanaEmFocoRepository) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.comunicadoService = comunicadoService;
        this.avisoService = avisoService;
        this.eventoService = eventoService;
        this.usuarioRepository = usuarioRepository;
        this.importacaoLogRepository = importacaoLogRepository;
        this.semanaEmFocoRepository = semanaEmFocoRepository;
    }

    public DashboardDTO coletarDados(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return switch (usuario.getPerfil()) {
            case ADMIN -> coletarDadosAdmin(usuario);
            case VICE_DIRETORA -> coletarDadosViceDiretora(usuario);
            case COORDENADORA -> coletarDadosCoordenadora(usuario);
        };
    }

    public DashboardDTO coletarDadosAdmin(Usuario usuario) {
        List<DemandaResumoDTO> demandas = DashboardMapper.toDemandaResumoDTOList(
                demandaService.listarTodasParaAdmin()
        );

        List<Segmento> todosSegmentos = perfilService.getSegmentosDoUsuario(usuario);
        List<SegmentoResumoDTO> segmentosResumo = todosSegmentos.stream()
                .map(seg -> {
                    var progresso = demandaService.calcularProgressoPorSegmento(
                            SegmentoCoordenacao.fromSlug(seg.getSlug())
                    );
                    return DashboardMapper.toSegmentoResumoDTO(
                            seg,
                            progresso.total(),
                            progresso.pendentes(),
                            progresso.emAndamento(),
                            progresso.concluidas()
                    );
                })
                .toList();

        SemanaFocoDTO semanaDto = semanaEmFocoService.buscarAtiva()
                .map(DashboardMapper::toSemanaFocoDTO)
                .orElse(SemanaFocoDTO.vazio());

        var resumo = demandaService.resumoGeral();

        IndicadoresDTO indicadores = new IndicadoresDTO(
                resumo.ativas(),
                resumo.pendentes(),
                resumo.proximasDoPrazo(),
                resumo.concluidas(),
                resumo.emAndamento(),
                resumo.total(),
                usuarioRepository.countByStatus(StatusUsuario.ATIVO),
                usuarioRepository.countByStatus(StatusUsuario.PENDENTE),
                usuarioRepository.count(),
                importacaoLogRepository.count()
        );

        return new DashboardDTO(
                usuario.getPerfil(),
                usuario.getNome(),
                usuario.getEmail(),
                indicadores,
                semanaDto,
                demandas,
                DashboardMapper.toComunicadoDTOList(comunicadoService.listarTodos()),
                DashboardMapper.toAvisoDTOList(avisoService.listarTodos()),
                DashboardMapper.toEventoDTOList(eventoService.listarTodos()),
                segmentosResumo,
                new PendenciasDTO(
                        resumo.pendentes(),
                        resumo.proximasDoPrazo(),
                        semanaEmFocoRepository.countAtivasSemRelatorio()
                )
        );
    }

    public DashboardDTO coletarDadosViceDiretora(Usuario usuario) {
        List<Segmento> todosSegmentos = perfilService.getSegmentosDoUsuario(usuario);
        List<SegmentoCoordenacao> segCoords = todosSegmentos.stream()
                .map(s -> SegmentoCoordenacao.fromSlug(s.getSlug()))
                .filter(s -> s != null)
                .toList();

        var resumo = demandaService.resumoPorSegmentos(segCoords);

        List<DemandaResumoDTO> demandas = DashboardMapper.toDemandaResumoDTOList(
                demandaService.listarAtivasPorSegmentos(segCoords)
        );

        List<SegmentoResumoDTO> segmentosResumo = todosSegmentos.stream()
                .map(seg -> {
                    var progresso = demandaService.calcularProgressoPorSegmento(
                            SegmentoCoordenacao.fromSlug(seg.getSlug())
                    );
                    return DashboardMapper.toSegmentoResumoDTO(
                            seg,
                            progresso.total(),
                            progresso.pendentes(),
                            progresso.emAndamento(),
                            progresso.concluidas()
                    );
                })
                .toList();

        SemanaFocoDTO semanaDto = semanaEmFocoService.buscarAtiva()
                .map(DashboardMapper::toSemanaFocoDTO)
                .orElse(SemanaFocoDTO.vazio());

        IndicadoresDTO indicadores = new IndicadoresDTO(
                resumo.ativas(),
                resumo.pendentes(),
                resumo.proximasDoPrazo(),
                resumo.concluidas(),
                resumo.emAndamento(),
                resumo.total(),
                0, 0, 0, 0
        );

        return new DashboardDTO(
                usuario.getPerfil(),
                usuario.getNome(),
                usuario.getEmail(),
                indicadores,
                semanaDto,
                demandas,
                DashboardMapper.toComunicadoDTOList(comunicadoService.listarTodos()),
                DashboardMapper.toAvisoDTOList(avisoService.listarTodos()),
                DashboardMapper.toEventoDTOList(eventoService.listarTodos()),
                segmentosResumo,
                new PendenciasDTO(
                        resumo.pendentes(),
                        resumo.proximasDoPrazo(),
                        semanaEmFocoRepository.countAtivasSemRelatorio()
                )
        );
    }

    public DashboardDTO coletarDadosCoordenadora(Usuario usuario) {
        List<Segmento> segmentosDoUsuario = perfilService.getSegmentosDoUsuario(usuario);
        List<SegmentoCoordenacao> segCoords = segmentosDoUsuario.stream()
                .map(s -> SegmentoCoordenacao.fromSlug(s.getSlug()))
                .filter(s -> s != null)
                .toList();

        var resumo = demandaService.resumoPorSegmentos(segCoords);

        List<DemandaResumoDTO> demandas = DashboardMapper.toDemandaResumoDTOList(
                demandaService.listarAtivasPorSegmentos(segCoords)
        );

        List<SegmentoResumoDTO> segmentosResumo = segmentosDoUsuario.stream()
                .map(seg -> {
                    SegmentoCoordenacao segEnum = SegmentoCoordenacao.fromSlug(seg.getSlug());
                    if (segEnum == null) {
                        return DashboardMapper.toSegmentoResumoDTO(seg, 0, 0, 0, 0);
                    }
                    var progresso = demandaService.calcularProgressoPorSegmento(segEnum);
                    return DashboardMapper.toSegmentoResumoDTO(
                            seg,
                            progresso.total(),
                            progresso.pendentes(),
                            progresso.emAndamento(),
                            progresso.concluidas()
                    );
                })
                .toList();

        List<SemanaFocoDTO> semanas = semanaEmFocoService.listarAtivasPorSegmentos(segCoords).stream()
                .map(DashboardMapper::toSemanaFocoDTO)
                .toList();
        SemanaFocoDTO semanaDto = semanas.isEmpty() ? SemanaFocoDTO.vazio() : semanas.get(0);

        return new DashboardDTO(
                usuario.getPerfil(),
                usuario.getNome(),
                usuario.getEmail(),
                new IndicadoresDTO(
                        resumo.ativas(),
                        resumo.pendentes(),
                        resumo.proximasDoPrazo(),
                        resumo.concluidas(),
                        resumo.emAndamento(),
                        resumo.total(),
                        0, 0, 0, 0
                ),
                semanaDto,
                demandas,
                DashboardMapper.toComunicadoDTOList(comunicadoService.listarTodos()),
                DashboardMapper.toAvisoDTOList(avisoService.listarGlobaisEPorSegmentos(segCoords)),
                DashboardMapper.toEventoDTOList(eventoService.listarPorSegmentosECompartilhados(segCoords)),
                segmentosResumo,
                new PendenciasDTO(
                        resumo.pendentes(),
                        resumo.proximasDoPrazo(),
                        semanaEmFocoRepository.countAtivasSemRelatorioPorSegmentos(segCoords)
                )
        );
    }
}

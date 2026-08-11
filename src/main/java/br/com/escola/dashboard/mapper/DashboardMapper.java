package br.com.escola.dashboard.mapper;

import br.com.escola.dashboard.dto.*;
import br.com.escola.dashboard.entity.*;
import br.com.escola.dashboard.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DashboardMapper {

    private static final DateTimeFormatter DATA_HORA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DashboardMapper() {
    }

    public static DemandaResumoDTO toDemandaResumoDTO(Demanda d) {
        boolean atrasada = d.getDataPrazo() != null
                && d.getDataPrazo().isBefore(LocalDate.now())
                && d.getStatus() != StatusDemanda.CONCLUIDA
                && d.getStatus() != StatusDemanda.CANCELADA;

        String prazoFormatado = d.getDataPrazo() != null
                ? d.getDataPrazo().format(DATA_FMT)
                : "Sem prazo";

        return new DemandaResumoDTO(
                d.getId(),
                d.getTitulo(),
                d.getDescricao(),
                d.getSegmento() != null ? d.getSegmento().getTitulo() : "Sem segmento",
                d.getSegmento() != null ? d.getSegmento().getSlug() : "",
                d.getPrioridade() != null ? d.getPrioridade().getTitulo() : "",
                d.getPrioridade() != null ? d.getPrioridade().getCssClass() : "",
                d.getStatus() != null ? d.getStatus().getTitulo() : "",
                d.getStatus() != null ? d.getStatus().getCssClass() : "",
                d.getDataPrazo(),
                prazoFormatado,
                d.getCriadaPor() != null ? d.getCriadaPor() : "Direcao",
                atrasada
        );
    }

    public static SemanaFocoDTO toSemanaFocoDTO(SemanaEmFoco s) {
        if (s == null) {
            return SemanaFocoDTO.vazio();
        }

        String periodo = "";
        if (s.getDataInicio() != null && s.getDataFim() != null) {
            periodo = s.getDataInicio().format(DATA_FMT) + " a " + s.getDataFim().format(DATA_FMT);
        }

        String statusRelatorio = "NAO_INICIADO";
        if (s.getRelatorio() != null) {
            statusRelatorio = s.getRelatorio().getStatus().name();
        }

        return new SemanaFocoDTO(
                s.getId(),
                s.getTitulo() != null ? s.getTitulo() : "",
                s.getDescricao() != null ? s.getDescricao() : "",
                s.getSegmento() != null ? s.getSegmento().getSlug() : "",
                s.getSegmento() != null ? s.getSegmento().getTitulo() : "",
                s.getPrioridade() != null ? s.getPrioridade().getTitulo() : "",
                s.getDataInicio(),
                s.getDataFim(),
                periodo,
                s.isAtiva(),
                statusRelatorio
        );
    }

    public static ComunicadoDTO toComunicadoDTO(Comunicado c) {
        return new ComunicadoDTO(
                c.getId(),
                c.getTitulo(),
                c.getConteudo(),
                c.getDataCriacao(),
                c.getDataCriacao() != null ? c.getDataCriacao().format(DATA_HORA_FMT) : ""
        );
    }

    public static AvisoDTO toAvisoDTO(Aviso a) {
        return new AvisoDTO(
                a.getId(),
                a.getTitulo(),
                a.getConteudo(),
                a.getPrioridade() != null ? a.getPrioridade().getTitulo() : "",
                a.getPrioridade() != null ? a.getPrioridade().getCssClass() : "",
                a.getSegmento() != null ? a.getSegmento().getSlug() : null,
                a.getSegmento() != null ? a.getSegmento().getTitulo() : "Geral",
                a.getDataCriacao(),
                a.getDataCriacao() != null ? a.getDataCriacao().format(DATA_HORA_FMT) : "",
                a.getSegmento() == null
        );
    }

    public static EventoDTO toEventoDTO(Evento e) {
        String dataFormatada = "";
        if (e.getDataInicio() != null) {
            if (e.getDataFim() != null && !e.getDataInicio().equals(e.getDataFim())) {
                dataFormatada = e.getDataInicio().format(DATA_FMT) + " a " + e.getDataFim().format(DATA_FMT);
            } else {
                dataFormatada = e.getDataInicio().format(DATA_FMT);
            }
        }

        return new EventoDTO(
                e.getId(),
                e.getTitulo(),
                e.getDescricao(),
                e.getDataInicio(),
                e.getDataFim(),
                e.isDiaInteiro(),
                e.getSegmento() != null ? e.getSegmento().getSlug() : null,
                e.getSegmento() != null ? e.getSegmento().getTitulo() : "Compartilhado",
                e.getGoogleEventId(),
                dataFormatada,
                e.getSegmento() == null
        );
    }

    public static SegmentoResumoDTO toSegmentoResumoDTO(Segmento seg, long total, long pendentes, long emAndamento, long concluidas) {
        return new SegmentoResumoDTO(
                seg.getId(),
                seg.getSlug(),
                seg.getTitulo(),
                seg.getDescricao(),
                total,
                pendentes,
                emAndamento,
                concluidas
        );
    }

    public static List<DemandaResumoDTO> toDemandaResumoDTOList(List<Demanda> demandas) {
        return demandas.stream().map(DashboardMapper::toDemandaResumoDTO).toList();
    }

    public static List<ComunicadoDTO> toComunicadoDTOList(List<Comunicado> comunicados) {
        return comunicados.stream().map(DashboardMapper::toComunicadoDTO).toList();
    }

    public static List<AvisoDTO> toAvisoDTOList(List<Aviso> avisos) {
        return avisos.stream().map(DashboardMapper::toAvisoDTO).toList();
    }

    public static List<EventoDTO> toEventoDTOList(List<Evento> eventos) {
        return eventos.stream().map(DashboardMapper::toEventoDTO).toList();
    }
}

package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.CsvPreviewDTO;
import br.com.escola.dashboard.dto.ImportacaoResultadoDTO;
import br.com.escola.dashboard.entity.Aviso;
import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.entity.Coordenadora;
import br.com.escola.dashboard.entity.comunicado;
import br.com.escola.dashboard.entity.Demanda;
import br.com.escola.dashboard.entity.Evento;
import br.com.escola.dashboard.entity.Professor;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PrioridadeCard;
import br.com.escola.dashboard.enums.PrioridadeDemanda;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.enums.StatusCard;
import br.com.escola.dashboard.enums.StatusDemanda;
import br.com.escola.dashboard.repository.AvisoRepository;
import br.com.escola.dashboard.repository.CardRepository;
import br.com.escola.dashboard.repository.CoordenadoraRepository;
import br.com.escola.dashboard.repository.ComunicadoRepository;
import br.com.escola.dashboard.repository.DemandaRepository;
import br.com.escola.dashboard.repository.EventoRepository;
import br.com.escola.dashboard.repository.ProfessorRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class CsvImportService {

    private final CoordenadoraRepository coordenadoraRepository;
    private final ProfessorRepository professorRepository;
    private final AvisoRepository avisoRepository;
    private final EventoRepository eventoRepository;
    private final CardRepository cardRepository;
    private final DemandaRepository demandaRepository;
    private final ComunicadoRepository comunicadoRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Map<String, List<String>> COLUNAS_OBRIGATORIAS = Map.of(
            "coordenadoras", List.of("nome", "segmento"),
            "professores", List.of("nome", "segmento"),
            "avisos", List.of("titulo"),
            "eventos", List.of("titulo", "data_inicio"),
            "cards", List.of("titulo", "categoria"),
            "demandas", List.of("titulo", "segmento"),
            "comunicados", List.of("titulo")
    );

    public CsvImportService(CoordenadoraRepository coordenadoraRepository,
                            ProfessorRepository professorRepository,
                            AvisoRepository avisoRepository,
                            EventoRepository eventoRepository,
                            CardRepository cardRepository,
                            DemandaRepository demandaRepository,
                            ComunicadoRepository comunicadoRepository) {
        this.coordenadoraRepository = coordenadoraRepository;
        this.professorRepository = professorRepository;
        this.avisoRepository = avisoRepository;
        this.eventoRepository = eventoRepository;
        this.cardRepository = cardRepository;
        this.demandaRepository = demandaRepository;
        this.comunicadoRepository = comunicadoRepository;
    }

    public CsvPreviewDTO preview(MultipartFile arquivo, String tipoEntidade) throws Exception {
        List<Map<String, String>> linhas = parseCsv(arquivo);
        CsvPreviewDTO preview = new CsvPreviewDTO();
        preview.setTipoEntidade(tipoEntidade);
        preview.setNomeArquivo(arquivo.getOriginalFilename());
        preview.setTotalLinhas(linhas.size());

        if (linhas.isEmpty()) {
            preview.getErrosValidacao().add("Arquivo CSV vazio ou sem dados.");
            return preview;
        }

        preview.setColunas(new ArrayList<>(linhas.get(0).keySet()));
        int previewSize = Math.min(5, linhas.size());
        preview.setLinhasPreview(linhas.subList(0, previewSize));

        List<String> erros = validarInteligente(linhas, tipoEntidade);
        preview.setErrosValidacao(erros);

        return preview;
    }

    @Transactional
    public ImportacaoResultadoDTO importar(MultipartFile arquivo, String tipoEntidade) throws Exception {
        long inicio = System.currentTimeMillis();

        List<Map<String, String>> linhas = parseCsv(arquivo);
        ImportacaoResultadoDTO resultado = new ImportacaoResultadoDTO(tipoEntidade);
        resultado.setTotalRegistros(linhas.size());

        for (int i = 0; i < linhas.size(); i++) {
            Map<String, String> linha = linhas.get(i);
            int numLinha = i + 2;

            try {
                switch (tipoEntidade) {
                    case "coordenadoras" -> importarCoordenadora(linha, resultado, numLinha);
                    case "professores" -> importarProfessor(linha, resultado, numLinha);
                    case "avisos" -> importarAviso(linha, resultado, numLinha);
                    case "eventos" -> importarEvento(linha, resultado, numLinha);
                    case "cards" -> importarCard(linha, resultado, numLinha);
                    case "demandas" -> importarDemanda(linha, resultado, numLinha);
                    case "comunicados" -> importarComunicado(linha, resultado, numLinha);
                    default -> {
                        resultado.adicionarErro("Tipo de entidade desconhecido: " + tipoEntidade);
                        resultado.setIgnorados(resultado.getIgnorados() + 1);
                    }
                }
            } catch (Exception e) {
                resultado.adicionarErroDetalhado(numLinha, "-", e.getMessage(), "-");
                resultado.setIgnorados(resultado.getIgnorados() + 1);
            }
        }

        long fim = System.currentTimeMillis();
        resultado.setTempoProcessamentoMs(fim - inicio);
        return resultado;
    }

    private List<String> validarInteligente(List<Map<String, String>> linhas, String tipoEntidade) {
        List<String> erros = new ArrayList<>();
        if (linhas.isEmpty()) return erros;

        Map<String, String> primeiraLinha = linhas.get(0);
        List<String> colunasEsperadas = COLUNAS_OBRIGATORIAS.getOrDefault(tipoEntidade, List.of());

        for (String colObrigatoria : colunasEsperadas) {
            if (!primeiraLinha.containsKey(colObrigatoria)) {
                erros.add("Coluna obrigatoria ausente: '" + colObrigatoria + "'");
            }
        }

        Set<String> colunasDesconhecidas = new HashSet<>(primeiraLinha.keySet());
        colunasDesconhecidas.removeAll(colunasEsperadas);

        Set<String> emailsVistos = new HashSet<>();

        for (int i = 0; i < linhas.size(); i++) {
            Map<String, String> linha = linhas.get(i);
            int numLinha = i + 2;

            String segmentoStr = linha.getOrDefault("segmento", "").trim();
            if (!segmentoStr.isEmpty() && parseSegmento(segmentoStr) == null) {
                erros.add("Linha " + numLinha + " - Segmento: Valor invalido '" + segmentoStr + "'");
            }

            String dataInicioStr = linha.getOrDefault("data_inicio", "").trim();
            String dataFimStr = linha.getOrDefault("data_fim", "").trim();
            String dataEventoStr = linha.getOrDefault("data_evento", "").trim();
            String dataPrazoStr = linha.getOrDefault("data_prazo", "").trim();

            for (String dataStr : List.of(dataInicioStr, dataFimStr, dataEventoStr, dataPrazoStr)) {
                if (!dataStr.isEmpty() && parseDate(dataStr) == null) {
                    erros.add("Linha " + numLinha + " - Data: Formato invalido '" + dataStr + "' (use dd/MM/yyyy ou yyyy-MM-dd)");
                }
            }

            String email = linha.getOrDefault("email", "").trim();
            if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                erros.add("Linha " + numLinha + " - Email: Formato invalido '" + email + "'");
            }

            if (!email.isEmpty() && !emailsVistos.add(email.toLowerCase())) {
                erros.add("Linha " + numLinha + " - Email: Duplicado '" + email + "'");
            }

            String titulo = linha.getOrDefault("titulo", "").trim();
            String nome = linha.getOrDefault("nome", "").trim();
            if (titulo.isEmpty() && nome.isEmpty()) {
                erros.add("Linha " + numLinha + " - Campo obrigatorio vazio: titulo ou nome");
            }
        }

        return erros;
    }

    private void importarCoordenadora(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String nome = linha.getOrDefault("nome", "").trim();
        String email = linha.getOrDefault("email", "").trim();
        String segmentoStr = linha.getOrDefault("segmento", "").trim();
        String telefone = linha.getOrDefault("telefone", "").trim();

        if (nome.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "nome", "Campo obrigatorio vazio", nome);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        SegmentoCoordenacao segmento = parseSegmento(segmentoStr);
        if (segmento == null && !segmentoStr.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "segmento", "Segmento invalido", segmentoStr);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        Optional<Coordenadora> existente = email.isEmpty() ? Optional.empty() : coordenadoraRepository.findByEmail(email);
        if (existente.isPresent()) {
            Coordenadora c = existente.get();
            c.setNome(nome);
            if (segmento != null) c.setSegmento(segmento);
            if (!telefone.isEmpty()) c.setTelefone(telefone);
            coordenadoraRepository.save(c);
            resultado.setAtualizados(resultado.getAtualizados() + 1);
        } else {
            Coordenadora c = new Coordenadora();
            c.setNome(nome);
            if (!email.isEmpty()) c.setEmail(email);
            if (segmento != null) c.setSegmento(segmento);
            if (!telefone.isEmpty()) c.setTelefone(telefone);
            coordenadoraRepository.save(c);
            resultado.setInseridos(resultado.getInseridos() + 1);
        }
    }

    private void importarProfessor(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String nome = linha.getOrDefault("nome", "").trim();
        String email = linha.getOrDefault("email", "").trim();
        String disciplina = linha.getOrDefault("disciplina", "").trim();
        String segmentoStr = linha.getOrDefault("segmento", "").trim();

        if (nome.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "nome", "Campo obrigatorio vazio", nome);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        SegmentoCoordenacao segmento = parseSegmento(segmentoStr);
        if (segmento == null && !segmentoStr.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "segmento", "Segmento invalido", segmentoStr);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        Professor p = new Professor();
        p.setNome(nome);
        if (!email.isEmpty()) p.setEmail(email);
        if (!disciplina.isEmpty()) p.setDisciplina(disciplina);
        if (segmento != null) p.setSegmento(segmento);
        professorRepository.save(p);
        resultado.setInseridos(resultado.getInseridos() + 1);
    }

    private void importarAviso(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String titulo = linha.getOrDefault("titulo", "").trim();
        String conteudo = linha.getOrDefault("conteudo", "").trim();
        String prioridadeStr = linha.getOrDefault("prioridade", "MEDIA").trim();
        String segmentoStr = linha.getOrDefault("segmento", "").trim();

        if (titulo.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "titulo", "Campo obrigatorio vazio", titulo);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        PrioridadeDemanda prioridade = parsePrioridadeDemanda(prioridadeStr);
        SegmentoCoordenacao segmento = parseSegmento(segmentoStr);

        Aviso a = new Aviso();
        a.setTitulo(titulo);
        if (!conteudo.isEmpty()) a.setConteudo(conteudo);
        a.setPrioridade(prioridade);
        a.setSegmento(segmento);
        avisoRepository.save(a);
        resultado.setInseridos(resultado.getInseridos() + 1);
    }

    private void importarEvento(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String titulo = linha.getOrDefault("titulo", "").trim();
        String descricao = linha.getOrDefault("descricao", "").trim();
        String dataInicioStr = linha.getOrDefault("data_inicio", "").trim();
        String dataFimStr = linha.getOrDefault("data_fim", "").trim();
        String diaInteiroStr = linha.getOrDefault("dia_inteiro", "true").trim();
        String segmentoStr = linha.getOrDefault("segmento", "").trim();
        String googleEventId = linha.getOrDefault("google_event_id", "").trim();

        if (titulo.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "titulo", "Campo obrigatorio vazio", titulo);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        LocalDate dataInicio = parseDate(dataInicioStr);
        if (dataInicio == null) {
            resultado.adicionarErroDetalhado(numLinha, "data_inicio", "Data invalida", dataInicioStr);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        LocalDate dataFim = dataFimStr.isEmpty() ? dataInicio : parseDate(dataFimStr);
        SegmentoCoordenacao segmento = parseSegmento(segmentoStr);

        Optional<Evento> existente = googleEventId.isEmpty() ? Optional.empty() : eventoRepository.findByGoogleEventId(googleEventId);
        if (existente.isPresent()) {
            Evento e = existente.get();
            e.setTitulo(titulo);
            if (!descricao.isEmpty()) e.setDescricao(descricao);
            e.setDataInicio(dataInicio);
            e.setDataFim(dataFim);
            e.setDiaInteiro(Boolean.parseBoolean(diaInteiroStr));
            e.setSegmento(segmento);
            eventoRepository.save(e);
            resultado.setAtualizados(resultado.getAtualizados() + 1);
        } else {
            Evento e = new Evento();
            e.setTitulo(titulo);
            if (!descricao.isEmpty()) e.setDescricao(descricao);
            e.setDataInicio(dataInicio);
            e.setDataFim(dataFim);
            e.setDiaInteiro(Boolean.parseBoolean(diaInteiroStr));
            e.setSegmento(segmento);
            if (!googleEventId.isEmpty()) e.setGoogleEventId(googleEventId);
            eventoRepository.save(e);
            resultado.setInseridos(resultado.getInseridos() + 1);
        }
    }

    private void importarCard(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String titulo = linha.getOrDefault("titulo", "").trim();
        String descricao = linha.getOrDefault("descricao", "").trim();
        String categoriaStr = linha.getOrDefault("categoria", "").trim();
        String prioridadeStr = linha.getOrDefault("prioridade", "MEDIA").trim();
        String statusStr = linha.getOrDefault("status", "PENDENTE").trim();
        String dataEventoStr = linha.getOrDefault("data_evento", "").trim();
        String responsavel = linha.getOrDefault("responsavel", "").trim();
        String observacoes = linha.getOrDefault("observacoes", "").trim();

        if (titulo.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "titulo", "Campo obrigatorio vazio", titulo);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        CategoriaCard categoria = parseCategoriaCard(categoriaStr);
        if (categoria == null) {
            resultado.adicionarErroDetalhado(numLinha, "categoria", "Categoria invalida", categoriaStr);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        PrioridadeCard prioridade = parsePrioridadeCard(prioridadeStr);
        StatusCard status = parseStatusCard(statusStr);
        LocalDate dataEvento = parseDate(dataEventoStr);

        Card c = new Card();
        c.setTitulo(titulo);
        if (!descricao.isEmpty()) c.setDescricao(descricao);
        c.setCategoria(categoria);
        c.setPrioridade(prioridade);
        c.setStatus(status);
        c.setDataEvento(dataEvento);
        if (!responsavel.isEmpty()) c.setResponsavel(responsavel);
        if (!observacoes.isEmpty()) c.setObservacoes(observacoes);
        c.setDataCriacao(LocalDateTime.now());
        cardRepository.save(c);
        resultado.setInseridos(resultado.getInseridos() + 1);
    }

    private void importarDemanda(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String titulo = linha.getOrDefault("titulo", "").trim();
        String descricao = linha.getOrDefault("descricao", "").trim();
        String segmentoStr = linha.getOrDefault("segmento", "").trim();
        String prioridadeStr = linha.getOrDefault("prioridade", "MEDIA").trim();
        String statusStr = linha.getOrDefault("status", "PENDENTE").trim();
        String dataPrazoStr = linha.getOrDefault("data_prazo", "").trim();
        String criadaPor = linha.getOrDefault("criada_por", "").trim();

        if (titulo.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "titulo", "Campo obrigatorio vazio", titulo);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        SegmentoCoordenacao segmento = parseSegmento(segmentoStr);
        if (segmento == null && !segmentoStr.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "segmento", "Segmento invalido", segmentoStr);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        PrioridadeDemanda prioridade = parsePrioridadeDemanda(prioridadeStr);
        StatusDemanda status = parseStatusDemanda(statusStr);
        LocalDate dataPrazo = parseDate(dataPrazoStr);

        Demanda d = new Demanda();
        d.setTitulo(titulo);
        if (!descricao.isEmpty()) d.setDescricao(descricao);
        if (segmento != null) d.setSegmento(segmento);
        d.setPrioridade(prioridade);
        d.setStatus(status);
        d.setDataPrazo(dataPrazo);
        if (!criadaPor.isEmpty()) d.setCriadaPor(criadaPor);
        demandaRepository.save(d);
        resultado.setInseridos(resultado.getInseridos() + 1);
    }

    private void importarComunicado(Map<String, String> linha, ImportacaoResultadoDTO resultado, int numLinha) {
        String titulo = linha.getOrDefault("titulo", "").trim();
        String conteudo = linha.getOrDefault("conteudo", "").trim();

        if (titulo.isEmpty()) {
            resultado.adicionarErroDetalhado(numLinha, "titulo", "Campo obrigatorio vazio", titulo);
            resultado.setIgnorados(resultado.getIgnorados() + 1);
            return;
        }

        comunicado c = new comunicado();
        c.setTitulo(titulo);
        if (!conteudo.isEmpty()) c.setConteudo(conteudo);
        comunicadoRepository.save(c);
        resultado.setInseridos(resultado.getInseridos() + 1);
    }

    private List<Map<String, String>> parseCsv(MultipartFile arquivo) throws Exception {
        List<Map<String, String>> linhas = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .withQuoteChar('"')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build())
                .build()) {

            String[] cabecalho = reader.readNext();
            if (cabecalho == null) {
                return linhas;
            }

            String[] colunas = Arrays.stream(cabecalho)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toArray(String[]::new);

            String[] linha;
            while ((linha = reader.readNext()) != null) {
                Map<String, String> registro = new HashMap<>();
                for (int i = 0; i < colunas.length && i < linha.length; i++) {
                    registro.put(colunas[i], linha[i] != null ? linha[i].trim() : "");
                }
                linhas.add(registro);
            }
        }

        return linhas;
    }

    private SegmentoCoordenacao parseSegmento(String valor) {
        if (valor == null || valor.isEmpty()) return null;
        String upper = valor.toUpperCase().replace(" ", "_").replace("Í", "I");
        try {
            return SegmentoCoordenacao.valueOf(upper);
        } catch (IllegalArgumentException e) {
            String lower = valor.toLowerCase().trim();
            return switch (lower) {
                case "educação infantil", "educaçao infantil", "infantil" -> SegmentoCoordenacao.EDUCACAO_INFANTIL;
                case "fund. anos iniciais", "fundamental 1", "fund1", "fund. i", "fundamental i" -> SegmentoCoordenacao.FUNDAMENTAL_1;
                case "fund. anos finais", "fundamental 2", "fund2", "fund. ii", "fundamental ii" -> SegmentoCoordenacao.FUNDAMENTAL_2;
                case "ensino médio", "ensino medio", "medio" -> SegmentoCoordenacao.ENSINO_MEDIO;
                case "bilingue", "bilíngue" -> SegmentoCoordenacao.BILINGUE;
                case "integral" -> SegmentoCoordenacao.INTEGRAL;
                default -> null;
            };
        }
    }

    private PrioridadeDemanda parsePrioridadeDemanda(String valor) {
        if (valor == null || valor.isEmpty()) return PrioridadeDemanda.MEDIA;
        String upper = valor.toUpperCase().trim();
        try {
            return PrioridadeDemanda.valueOf(upper);
        } catch (IllegalArgumentException e) {
            return switch (upper) {
                case "BAIXA", "BAIXO" -> PrioridadeDemanda.BAIXA;
                case "MEDIA", "MÉDIO" -> PrioridadeDemanda.MEDIA;
                case "ALTA", "ALTO" -> PrioridadeDemanda.ALTA;
                case "URGENTE" -> PrioridadeDemanda.URGENTE;
                default -> PrioridadeDemanda.MEDIA;
            };
        }
    }

    private PrioridadeCard parsePrioridadeCard(String valor) {
        if (valor == null || valor.isEmpty()) return PrioridadeCard.MEDIA;
        try {
            return PrioridadeCard.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return PrioridadeCard.MEDIA;
        }
    }

    private StatusCard parseStatusCard(String valor) {
        if (valor == null || valor.isEmpty()) return StatusCard.PENDENTE;
        try {
            return StatusCard.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return StatusCard.PENDENTE;
        }
    }

    private StatusDemanda parseStatusDemanda(String valor) {
        if (valor == null || valor.isEmpty()) return StatusDemanda.PENDENTE;
        try {
            return StatusDemanda.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return StatusDemanda.PENDENTE;
        }
    }

    private CategoriaCard parseCategoriaCard(String valor) {
        if (valor == null || valor.isEmpty()) return null;
        try {
            return CategoriaCard.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return switch (valor.toLowerCase().trim()) {
                case "evento" -> CategoriaCard.EVENTO;
                case "falta professor", "falta_professor" -> CategoriaCard.FALTA_PROFESSOR;
                case "substituicao", "substituição" -> CategoriaCard.SUBSTITUICAO;
                case "rotina administrativa", "rotina_administrativa" -> CategoriaCard.ROTINA_ADMINISTRATIVA;
                case "rotina coordenadores", "rotina_coordenadores" -> CategoriaCard.ROTINA_COORDENADORES;
                case "semana em foco", "semana_em_foco" -> CategoriaCard.SEMANA_EM_FOCO;
                default -> null;
            };
        }
    }

    private LocalDate parseDate(String valor) {
        if (valor == null || valor.isEmpty()) return null;
        String trimmed = valor.trim();

        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER_2);
        } catch (DateTimeParseException ignored) {
        }

        return null;
    }
}

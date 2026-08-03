package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CsvPreviewDTO;
import br.com.escola.dashboard.dto.ImportacaoResultadoDTO;
import br.com.escola.dashboard.entity.ImportacaoLog;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.CsvImportService;
import br.com.escola.dashboard.service.ImportacaoLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/admin/importacao")
public class CsvImportController {

    private final CsvImportService csvImportService;
    private final ImportacaoLogService importacaoLogService;
    private final AdminAuthService adminAuthService;

    private static final List<String> TIPOS_VALIDOS = List.of(
            "coordenadoras", "professores", "avisos", "eventos",
            "cards", "demandas", "comunicados"
    );

    public CsvImportController(CsvImportService csvImportService,
                               ImportacaoLogService importacaoLogService,
                               AdminAuthService adminAuthService) {
        this.csvImportService = csvImportService;
        this.importacaoLogService = importacaoLogService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping
    public String exibirDashboard(@AuthenticationPrincipal OAuth2User usuario,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        model.addAttribute("tiposEntidade", TIPOS_VALIDOS);
        model.addAttribute("ultimaImportacao", importacaoLogService.buscarUltima().orElse(null));
        model.addAttribute("historico", importacaoLogService.listarRecentes());
        return "importacao-dados";
    }

    @PostMapping("/preview")
    public String preview(@AuthenticationPrincipal OAuth2User usuario,
                          @RequestParam("arquivo") MultipartFile arquivo,
                          @RequestParam("tipoEntidade") String tipoEntidade,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        if (arquivo.isEmpty() || arquivo.getOriginalFilename() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Selecione um arquivo CSV.");
            return "redirect:/admin/importacao";
        }

        if (!TIPOS_VALIDOS.contains(tipoEntidade)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Tipo de entidade invalido.");
            return "redirect:/admin/importacao";
        }

        String nomeArquivo = arquivo.getOriginalFilename();
        if (!nomeArquivo.toLowerCase().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "O arquivo deve ser um CSV.");
            return "redirect:/admin/importacao";
        }

        try {
            CsvPreviewDTO preview = csvImportService.preview(arquivo, tipoEntidade);
            model.addAttribute("preview", preview);
            model.addAttribute("tiposEntidade", TIPOS_VALIDOS);
            model.addAttribute("arquivoOriginal", nomeArquivo);
            model.addAttribute("tipoSelecionado", tipoEntidade);
            model.addAttribute("ultimaImportacao", importacaoLogService.buscarUltima().orElse(null));
            model.addAttribute("historico", importacaoLogService.listarRecentes());
            return "importacao-dados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao ler o arquivo: " + e.getMessage());
            return "redirect:/admin/importacao";
        }
    }

    @PostMapping("/executar")
    public String executar(@AuthenticationPrincipal OAuth2User usuario,
                           @RequestParam("arquivo") MultipartFile arquivo,
                           @RequestParam("tipoEntidade") String tipoEntidade,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        if (arquivo.isEmpty() || arquivo.getOriginalFilename() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Selecione um arquivo CSV.");
            return "redirect:/admin/importacao";
        }

        if (!TIPOS_VALIDOS.contains(tipoEntidade)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Tipo de entidade invalido.");
            return "redirect:/admin/importacao";
        }

        try {
            ImportacaoResultadoDTO resultado = csvImportService.importar(arquivo, tipoEntidade);

            ImportacaoLog log = new ImportacaoLog();
            log.setTipoEntidade(tipoEntidade);
            log.setNomeArquivo(arquivo.getOriginalFilename());
            log.setUsuario(email != null ? email : "sistema");
            log.setTotalRegistros(resultado.getTotalRegistros());
            log.setInseridos(resultado.getInseridos());
            log.setAtualizados(resultado.getAtualizados());
            log.setIgnorados(resultado.getIgnorados());
            log.setTotalErros(resultado.getTotalErros());
            log.setStatus(resultado.temErros() ? "ERROS" : "SUCESSO");
            log.setTempoProcessamentoMs(resultado.getTempoProcessamentoMs());
            log.setErrosDetalhados(resultado.gerarCsvErros());
            importacaoLogService.salvar(log);

            model.addAttribute("resultado", resultado);
            model.addAttribute("tiposEntidade", TIPOS_VALIDOS);
            model.addAttribute("ultimaImportacao", importacaoLogService.buscarUltima().orElse(null));
            model.addAttribute("historico", importacaoLogService.listarRecentes());
            return "importacao-dados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao importar: " + e.getMessage());
            return "redirect:/admin/importacao";
        }
    }

    @GetMapping("/erros/download")
    public void downloadErros(@AuthenticationPrincipal OAuth2User usuario,
                              @RequestParam Long logId,
                              HttpServletResponse response,
                              RedirectAttributes redirectAttributes) throws IOException {
        String email = usuario != null ? usuario.getAttribute("email") : null;

        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return;
        }

        var logOpt = importacaoLogService.buscarUltima();
        // For simplicity, always download the latest log's errors
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=erros_importacao.csv");

        PrintWriter writer = response.getWriter();
        writer.println("Linha;Erro;Campo;Valor Recebido");

        String errosCsv = logOpt.map(ImportacaoLog::getErrosDetalhados).orElse("");
        String[] linhas = errosCsv.split("\n");
        for (int i = 1; i < linhas.length; i++) {
            writer.println(linhas[i]);
        }
        writer.flush();
    }
}

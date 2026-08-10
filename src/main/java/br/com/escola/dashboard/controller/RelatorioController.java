package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.RelatorioSemanaEmFocoDTO;
import br.com.escola.dashboard.entity.RelatorioSemanaEmFoco;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.RelatorioSemanaEmFocoService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import br.com.escola.dashboard.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    private final RelatorioSemanaEmFocoService relatorioService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final AdminAuthService adminAuthService;
    private final UsuarioService usuarioService;

    public RelatorioController(RelatorioSemanaEmFocoService relatorioService,
                               SemanaEmFocoService semanaEmFocoService,
                               AdminAuthService adminAuthService,
                               UsuarioService usuarioService) {
        this.relatorioService = relatorioService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.adminAuthService = adminAuthService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{semanaId}")
    public String obterOuCriar(@PathVariable Long semanaId,
                              @AuthenticationPrincipal OAuth2User usuario,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        String coordenadoraId = usuario.getAttribute("email");
        String coordenadoraNome = usuario.getAttribute("name");

        RelatorioSemanaEmFoco relatorio = relatorioService.criarOuObter(
                semana.get(),
                coordenadoraId,
                coordenadoraNome,
                coordenadoraId
        );

        model.addAttribute("relatorio", relatorio);
        model.addAttribute("semana", semana.get());
        model.addAttribute("usuario", usuario);

        return "relatorio-semana-form";
    }

    @PostMapping("/{semanaId}")
    public String salvarRascunho(@PathVariable Long semanaId,
                                @AuthenticationPrincipal OAuth2User usuario,
                                @Valid @ModelAttribute("relatorio") RelatorioSemanaEmFocoDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String coordenadoraId = usuario.getAttribute("email");

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorioBusca = relatorioService.obterPorSemanaId(semanaId);

        if (relatorioBusca.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        RelatorioSemanaEmFoco relatorio = relatorioBusca.get();

        try {
            relatorioService.atualizar(relatorio.getId(), dto, coordenadoraId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio salvo com sucesso");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Voce nao tem permissao para editar este relatorio");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/" + semanaId;
    }

    @PostMapping("/{semanaId}/finalizar")
    public String finalizar(@PathVariable Long semanaId,
                           @AuthenticationPrincipal OAuth2User usuario,
                           RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String coordenadoraId = usuario.getAttribute("email");
        String coordenadoraNome = usuario.getAttribute("name");

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorioBusca = relatorioService.obterPorSemanaId(semanaId);

        if (relatorioBusca.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        try {
            relatorioService.finalizar(relatorioBusca.get().getId(), coordenadoraId, coordenadoraNome);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio finalizado com sucesso");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Voce nao tem permissao para finalizar este relatorio");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/" + semanaId;
    }

    @GetMapping("/visualizar/{semanaId}")
    public String visualizar(@PathVariable Long semanaId,
                            @AuthenticationPrincipal OAuth2User usuario,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<SemanaEmFoco> semana = semanaEmFocoService.buscarAtiva();
        if (semana.isEmpty() || !semana.get().getId().equals(semanaId)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Semana em Foco nao disponivel");
            return "redirect:/admin";
        }

        Optional<RelatorioSemanaEmFoco> relatorio = relatorioService.obterPorSemanaId(semanaId);

        if (relatorio.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/admin";
        }

        model.addAttribute("relatorio", relatorio.get());
        model.addAttribute("semana", semana.get());
        model.addAttribute("usuario", usuario);

        return "relatorio-semana-view";
    }

    @GetMapping("/admin/relatorios")
    public String listarParaAdmin(@AuthenticationPrincipal OAuth2User usuario,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        List<RelatorioSemanaEmFoco> relatorios = relatorioService.obterTodos();
        model.addAttribute("relatorios", relatorios);

        return "relatorios-lista-admin";
    }

    @GetMapping("/admin/relatorios/novo")
    public String exibirFormularioNovo(@AuthenticationPrincipal OAuth2User usuario,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        List<SemanaEmFoco> semanas = semanaEmFocoService.listarTodas();
        model.addAttribute("relatorio", new RelatorioSemanaEmFoco());
        model.addAttribute("semanas", semanas);
        model.addAttribute("semanaSelecionada", null);
        model.addAttribute("modoEdicao", false);
        return "relatorio-admin-form";
    }

    @PostMapping("/admin/relatorios/novo")
    public String salvarNovo(@AuthenticationPrincipal OAuth2User usuario,
                             @RequestParam Long semanaEmFocoId,
                             @RequestParam(required = false) String resumoSemana,
                             @RequestParam(required = false) String atividadesExecutadas,
                             @RequestParam(required = false) String pendencias,
                             @RequestParam(required = false) String observacoes,
                             @RequestParam(required = false) String conclusao,
                             RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        String nome = usuario.getAttribute("name");

        try {
            relatorioService.criarAdmin(semanaEmFocoId, email, nome, email,
                    resumoSemana, atividadesExecutadas, pendencias, observacoes, conclusao);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio criado com sucesso");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/admin/relatorios";
    }

    @GetMapping("/admin/relatorios/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Long id,
                                         @AuthenticationPrincipal OAuth2User usuario,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        Optional<RelatorioSemanaEmFoco> relatorio = relatorioService.obterPorId(id);
        if (relatorio.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatorio nao encontrado");
            return "redirect:/relatorio/admin/relatorios";
        }

        List<SemanaEmFoco> semanas = semanaEmFocoService.listarTodas();
        model.addAttribute("relatorio", relatorio.get());
        model.addAttribute("semanas", semanas);
        model.addAttribute("semanaSelecionada", relatorio.get().getSemanaEmFoco().getId());
        model.addAttribute("modoEdicao", true);
        return "relatorio-admin-form";
    }

    @PostMapping("/admin/relatorios/{id}/editar")
    public String salvarEdicao(@PathVariable Long id,
                               @AuthenticationPrincipal OAuth2User usuario,
                               @RequestParam Long semanaEmFocoId,
                               @RequestParam(required = false) String resumoSemana,
                               @RequestParam(required = false) String atividadesExecutadas,
                               @RequestParam(required = false) String pendencias,
                               @RequestParam(required = false) String observacoes,
                               @RequestParam(required = false) String conclusao,
                               RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        try {
            relatorioService.atualizarAdmin(id, semanaEmFocoId,
                    resumoSemana, atividadesExecutadas, pendencias, observacoes, conclusao);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio atualizado com sucesso");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/admin/relatorios";
    }

    @PostMapping("/admin/relatorios/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          @AuthenticationPrincipal OAuth2User usuario,
                          RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado");
            return "redirect:/dashboard";
        }

        try {
            relatorioService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatorio excluido com sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/relatorio/admin/relatorios";
    }
}

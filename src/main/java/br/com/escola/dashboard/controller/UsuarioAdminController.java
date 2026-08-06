package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.UsuarioRequestDTO;
import br.com.escola.dashboard.entity.Segmento;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.SegmentoService;
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

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;
    private final SegmentoService segmentoService;
    private final AdminAuthService adminAuthService;

    public UsuarioAdminController(UsuarioService usuarioService,
                                   SegmentoService segmentoService,
                                   AdminAuthService adminAuthService) {
        this.usuarioService = usuarioService;
        this.segmentoService = segmentoService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping
    public String listarUsuarios(@AuthenticationPrincipal OAuth2User usuario,
                                  @RequestParam(required = false) String termo,
                                  @RequestParam(required = false) PerfilUsuario perfil,
                                  @RequestParam(required = false) StatusUsuario status,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        List<Usuario> usuarios = usuarioService.buscarComFiltros(termo, perfil, status);

        model.addAttribute("usuarios", usuarios.stream()
                .map(usuarioService::converterParaDTO)
                .toList());
        model.addAttribute("termoBusca", termo);
        model.addAttribute("perfilFiltro", perfil);
        model.addAttribute("statusFiltro", status);
        model.addAttribute("perfis", PerfilUsuario.values());
        model.addAttribute("statusList", StatusUsuario.values());
        model.addAttribute("contarPorStatus", usuarioService.contarPorStatus());
        model.addAttribute("contarPorPerfil", usuarioService.contarPorPerfil());
        model.addAttribute("totalSegmentos", segmentoService.listarAtivos().size());

        return "admin/usuarios-admin";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@AuthenticationPrincipal OAuth2User usuario,
                                          @PathVariable Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        Usuario usuarioEditado = usuarioService.buscarPorIdOuErro(id);
        List<Segmento> todosSegmentos = segmentoService.listarAtivos();
        List<Segmento> segmentosUsuario = usuarioService.buscarSegmentosDoUsuario(id);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome(usuarioEditado.getNome());
        requestDTO.setPerfil(usuarioEditado.getPerfil());
        requestDTO.setStatus(usuarioEditado.getStatus());
        requestDTO.setSegmentoIds(segmentosUsuario.stream()
                .map(Segmento::getId)
                .toList());

        model.addAttribute("usuario", usuarioEditado);
        model.addAttribute("requestDTO", requestDTO);
        model.addAttribute("todosSegmentos", todosSegmentos);
        model.addAttribute("segmentosUsuarioIds", segmentosUsuario.stream()
                .map(Segmento::getId)
                .toList());
        model.addAttribute("perfis", PerfilUsuario.values());
        model.addAttribute("statusList", StatusUsuario.values());
        model.addAttribute("modoEdicao", true);

        return "admin/usuario-editar";
    }

    @PostMapping("/{id}/editar")
    public String salvarEdicao(@AuthenticationPrincipal OAuth2User usuario,
                                @PathVariable Long id,
                                @Valid @ModelAttribute("requestDTO") UsuarioRequestDTO requestDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            Usuario usuarioEditado = usuarioService.buscarPorIdOuErro(id);
            model.addAttribute("usuario", usuarioEditado);
            model.addAttribute("todosSegmentos", segmentoService.listarAtivos());
            model.addAttribute("segmentosUsuarioIds", usuarioService.buscarSegmentosDoUsuario(id).stream()
                    .map(Segmento::getId)
                    .toList());
            model.addAttribute("perfis", PerfilUsuario.values());
            model.addAttribute("statusList", StatusUsuario.values());
            model.addAttribute("modoEdicao", true);
            return "admin/usuario-editar";
        }

        usuarioService.atualizar(id, requestDTO);

        if (requestDTO.getSegmentoIds() != null) {
            usuarioService.sincronizarSegmentos(id, requestDTO.getSegmentoIds());
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuario atualizado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/aprovar")
    public String aprovarUsuario(@AuthenticationPrincipal OAuth2User usuario,
                                  @PathVariable Long id,
                                  @RequestParam PerfilUsuario perfil,
                                  @RequestParam(required = false) List<Long> segmentoIds,
                                  RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        usuarioService.aprovar(id, perfil, segmentoIds);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuario aprovado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/bloquear")
    public String bloquearUsuario(@AuthenticationPrincipal OAuth2User usuario,
                                   @PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        usuarioService.bloquear(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuario bloqueado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/desbloquear")
    public String desbloquearUsuario(@AuthenticationPrincipal OAuth2User usuario,
                                      @PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        usuarioService.desbloquear(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuario desbloqueado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/pendentes")
    public String listarPendentes(@AuthenticationPrincipal OAuth2User usuario,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        String email = usuario != null ? usuario.getAttribute("email") : null;
        if (!adminAuthService.isAdminEmailAuthorized(email)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/";
        }

        List<Usuario> pendentes = usuarioService.listarPorStatus(StatusUsuario.PENDENTE);

        model.addAttribute("usuarios", pendentes.stream()
                .map(usuarioService::converterParaDTO)
                .toList());
        model.addAttribute("todosSegmentos", segmentoService.listarAtivos());
        model.addAttribute("perfis", PerfilUsuario.values());
        model.addAttribute("pendenteMode", true);

        return "admin/usuarios-admin";
    }
}

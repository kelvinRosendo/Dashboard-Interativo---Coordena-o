package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.entity.SemanaEmFoco;
import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.SegmentoCoordenacao;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.CardService;
import br.com.escola.dashboard.service.ComunicadoService;
import br.com.escola.dashboard.service.DemandaService;
import br.com.escola.dashboard.service.SemanaEmFocoService;
import br.com.escola.dashboard.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class CoordenadoraController {

    private static final List<SegmentoCoordenadora> SEGMENTOS = Arrays.stream(SegmentoCoordenacao.values())
            .map(segmento -> new SegmentoCoordenadora(
                    segmento.getSlug(),
                    segmento.getTitulo(),
                    segmento.getDescricao()
            ))
            .toList();

    private final CardService cardService;
    private final DemandaService demandaService;
    private final SemanaEmFocoService semanaEmFocoService;
    private final ComunicadoService comunicadoService;
    private final AdminAuthService adminAuthService;
    private final UsuarioService usuarioService;

    public CoordenadoraController(CardService cardService,
                                  DemandaService demandaService,
                                  SemanaEmFocoService semanaEmFocoService,
                                  ComunicadoService comunicadoService,
                                  AdminAuthService adminAuthService,
                                  UsuarioService usuarioService) {
        this.cardService = cardService;
        this.demandaService = demandaService;
        this.semanaEmFocoService = semanaEmFocoService;
        this.comunicadoService = comunicadoService;
        this.adminAuthService = adminAuthService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/coordenadoras")
    public String listarCoordenadoras(@AuthenticationPrincipal OAuth2User usuario,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null || usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
            return "redirect:/dashboard";
        }

        model.addAttribute("segmentos", SEGMENTOS);
        return "coordenadoras";
    }

    @GetMapping("/coordenadoras/{slug}")
    public String painelCoordenadora(@AuthenticationPrincipal OAuth2User usuario,
                                     @PathVariable String slug,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        String email = usuario.getAttribute("email");
        Usuario usuarioAtual = usuarioService.buscarPorEmail(email);

        if (usuarioAtual == null) {
            return "redirect:/login";
        }

        SegmentoCoordenacao segmentoEnum = SegmentoCoordenacao.fromSlug(slug);

        if (segmentoEnum == null) {
            return "redirect:/coordenadoras";
        }

        boolean podeAcessar = false;
        if (usuarioAtual.getPerfil() == PerfilUsuario.ADMIN) {
            podeAcessar = true;
        } else if (usuarioAtual.getPerfil() == PerfilUsuario.COORDENADORA) {
            podeAcessar = usuarioService.buscarSegmentosDoUsuario(usuarioAtual.getId()).stream()
                    .anyMatch(s -> s.getSlug().equals(segmentoEnum.getSlug()));
        }

        if (!podeAcessar) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado. Voce nao tem permissao para acessar este segmento.");
            return "redirect:/coordenadoras";
        }

        SegmentoCoordenadora segmento = new SegmentoCoordenadora(
                segmentoEnum.getSlug(),
                segmentoEnum.getTitulo(),
                segmentoEnum.getDescricao()
        );

        List<CardResponseDTO> semanas = cardService.listarPorCategoria(CategoriaCard.SEMANA_EM_FOCO).stream()
                .filter(card -> contemSegmento(card, segmento.nome()))
                .toList();

        Optional<SemanaEmFoco> semanaAtivaOpt = semanaEmFocoService.buscarAtiva();
        SemanaEmFoco semanaEmFoco = null;
        if (semanaAtivaOpt.isPresent() && semanaAtivaOpt.get().getSegmento() == segmentoEnum) {
            semanaEmFoco = semanaAtivaOpt.get();
        }

        model.addAttribute("semanaEmFoco", semanaEmFoco);
        model.addAttribute("segmento", segmento);
        model.addAttribute("segmentoEnum", segmentoEnum);
        model.addAttribute("semanas", semanas);

        model.addAttribute("tarefas", cardService.listarPorCategoria(CategoriaCard.ROTINA_COORDENADORES));
        model.addAttribute("comunicados", comunicadoService.listarTodos());
        model.addAttribute("faltas", cardService.listarPorCategoria(CategoriaCard.FALTA_PROFESSOR));
        model.addAttribute("demandas", demandaService.listarPorSegmento(segmentoEnum));
        model.addAttribute("demandasAtivas", demandaService.listarAtivasPorSegmento(segmentoEnum));
        model.addAttribute("demandaProgresso", demandaService.calcularProgressoPorSegmento(segmentoEnum));
        model.addAttribute("demandasPendentes", demandaService.contarPendentesPorSegmento(segmentoEnum));
        model.addAttribute("demandasNovas", demandaService.listarNovasPendentesPorSegmento(segmentoEnum));
        model.addAttribute("quantidadeDemandasNovas", demandaService.contarNovasPendentesPorSegmento(segmentoEnum));

        return "coordenadora";
    }

    private boolean contemSegmento(CardResponseDTO card, String segmento) {
        String alvo = segmento.toLowerCase(Locale.ROOT);
        return contem(card.getTitulo(), alvo)
                || contem(card.getDescricao(), alvo)
                || contem(card.getResponsavel(), alvo);
    }

    private boolean contem(String texto, String alvo) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(alvo);
    }

    public record SegmentoCoordenadora(String slug, String nome, String descricao) {
    }
}

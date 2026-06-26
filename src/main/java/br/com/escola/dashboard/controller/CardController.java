package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.service.AdminAuthService;
import br.com.escola.dashboard.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final AdminAuthService adminAuthService;

    public CardController(CardService cardService, AdminAuthService adminAuthService) {
        this.cardService = cardService;
        this.adminAuthService = adminAuthService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> listarTodos() {
        List<CardResponseDTO> cards = cardService.listarTodos();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> buscarPorId(@PathVariable Long id) {
        CardResponseDTO card = cardService.buscarPorId(id);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<?> criarCard(@AuthenticationPrincipal OAuth2User usuario,
                                       @Valid @RequestBody CardRequestDTO requestDTO) {
        if (!isAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        CardResponseDTO response = cardService.criarCard(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCard(@AuthenticationPrincipal OAuth2User usuario,
                                           @PathVariable Long id,
                                           @Valid @RequestBody CardRequestDTO requestDTO) {
        if (!isAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        CardResponseDTO cardAtualizado = cardService.atualizarCard(id, requestDTO);
        return ResponseEntity.ok(cardAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCard(@AuthenticationPrincipal OAuth2User usuario,
                                         @PathVariable Long id) {
        if (!isAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        cardService.deletarCard(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        return adminAuthService.isAdminEmailAuthorized(usuario.getAttribute("email"));
    }
}

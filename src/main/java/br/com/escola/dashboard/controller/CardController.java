package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDTO> criarCard(@Valid @RequestBody CardRequestDTO requestDTO) {
        CardResponseDTO response = cardService.criarCard(requestDTO);
        return ResponseEntity.ok(response);
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

    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDTO> atualizarCard(
            @PathVariable Long id,
            @Valid @RequestBody CardRequestDTO requestDTO) {

        CardResponseDTO cardAtualizado = cardService.atualizarCard(id, requestDTO);
        return ResponseEntity.ok(cardAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return ResponseEntity.noContent().build();
    }
}

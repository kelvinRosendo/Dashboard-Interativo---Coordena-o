package br.com.escola.dashboard.controller;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🔹 CardController
 *
 * Essa classe recebe as requisições HTTP do usuário
 * e chama o CardService para executar as operações.
 */
@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    // 🔹 Injeção de dependência
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 🔹 Criar um novo card
     *
     * POST /cards
     */
    @PostMapping
    public ResponseEntity<CardResponseDTO> criarCard(@RequestBody CardRequestDTO requestDTO) {
        CardResponseDTO response = cardService.criarCard(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 Listar todos os cards
     *
     * GET /cards
     */
    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> listarTodos() {
        List<CardResponseDTO> cards = cardService.listarTodos();
        return ResponseEntity.ok(cards);
    }

    /**
     * 🔹 Buscar card por ID
     *
     * GET /cards/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> buscarPorId(@PathVariable Long id) {
        CardResponseDTO card = cardService.buscarPorId(id);
        return ResponseEntity.ok(card);
    }

    /**
     * 🔹 Atualizar um card
     *
     * PUT /cards/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDTO> atualizarCard(
            @PathVariable Long id,
            @RequestBody CardRequestDTO requestDTO) {

        CardResponseDTO cardAtualizado = cardService.atualizarCard(id, requestDTO);
        return ResponseEntity.ok(cardAtualizado);
    }

    /**
     * 🔹 Deletar um card
     *
     * DELETE /cards/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCard(@PathVariable Long id) {
        cardService.deletarCard(id);
        return ResponseEntity.noContent().build();
    }
}
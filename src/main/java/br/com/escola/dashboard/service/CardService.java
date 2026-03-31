package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 🔹 CardService
 *
 * Essa classe é a camada de regra de negócio dos cards.
 * Ela fica entre o Controller e o Repository.
 *
 * Funções principais:
 * - receber os dados enviados pelo controller
 * - aplicar a lógica da aplicação
 * - conversar com o repository
 * - retornar os dados já organizados em DTO
 */
@Service
public class CardService {

    private final CardRepository cardRepository;

    /**
     * 🔹 Injeção de dependência via construtor
     *
     * O Spring injeta automaticamente o CardRepository aqui.
     */
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * 🔹 Criar um novo card
     *
     * Recebe um CardRequestDTO com os dados vindos da requisição,
     * transforma em entidade Card, salva no banco e devolve um CardResponseDTO.
     */
    public CardResponseDTO criarCard(CardRequestDTO requestDTO) {
        Card card = new Card();

        card.setTitulo(requestDTO.getTitulo());
        card.setDescricao(requestDTO.getDescricao());
        card.setCategoria(requestDTO.getCategoria());
        card.setPrioridade(requestDTO.getPrioridade());
        card.setDataCriacao(LocalDateTime.now());

        Card cardSalvo = cardRepository.save(card);

        return converterParaResponseDTO(cardSalvo);
    }

    /**
     * 🔹 Listar todos os cards
     *
     * Busca todos os cards no banco e converte cada um para CardResponseDTO.
     */
    public List<CardResponseDTO> listarTodos() {
        List<Card> cards = cardRepository.findAll();

        return cards.stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    /**
     * 🔹 Buscar card por ID
     *
     * Procura um card pelo id.
     * Se não encontrar, lança ResourceNotFoundException.
     */
    public CardResponseDTO buscarPorId(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado com id: " + id));

        return converterParaResponseDTO(card);
    }

    /**
     * 🔹 Atualizar um card existente
     *
     * Primeiro busca o card no banco.
     * Se existir, atualiza os campos com os dados do DTO.
     * Depois salva novamente e retorna o card atualizado.
     */
    public CardResponseDTO atualizarCard(Long id, CardRequestDTO requestDTO) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado com id: " + id));

        card.setTitulo(requestDTO.getTitulo());
        card.setDescricao(requestDTO.getDescricao());
        card.setCategoria(requestDTO.getCategoria());
        card.setPrioridade(requestDTO.getPrioridade());

        Card cardAtualizado = cardRepository.save(card);

        return converterParaResponseDTO(cardAtualizado);
    }

    /**
     * 🔹 Deletar um card
     *
     * Primeiro verifica se o card existe.
     * Se não existir, lança exceção.
     * Se existir, remove do banco.
     */
    public void deletarCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado com id: " + id));

        cardRepository.delete(card);
    }

    /**
     * 🔹 Método auxiliar para converter Card em CardResponseDTO
     *
     * Isso evita repetir código em vários métodos.
     */
    private CardResponseDTO converterParaResponseDTO(Card card) {
        CardResponseDTO responseDTO = new CardResponseDTO();

        responseDTO.setId(card.getId());
        responseDTO.setTitulo(card.getTitulo());
        responseDTO.setDescricao(card.getDescricao());
        responseDTO.setCategoria(card.getCategoria());
        responseDTO.setPrioridade(card.getPrioridade());
        responseDTO.setDataCriacao(card.getDataCriacao());

        return responseDTO;
    }
}
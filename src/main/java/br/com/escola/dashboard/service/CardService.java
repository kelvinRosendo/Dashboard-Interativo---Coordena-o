package br.com.escola.dashboard.service;

import br.com.escola.dashboard.dto.CardRequestDTO;
import br.com.escola.dashboard.dto.CardResponseDTO;
import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.enums.CategoriaCard;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CardResponseDTO criarCard(CardRequestDTO requestDTO) {
        validarRegrasDeNegocio(requestDTO);

        Card card = new Card();
        preencherCard(card, requestDTO);
        card.setDataCriacao(LocalDateTime.now());

        Card cardSalvo = cardRepository.save(card);
        return converterParaResponseDTO(cardSalvo);
    }

    public List<CardResponseDTO> listarTodos() {
        List<Card> cards = cardRepository.findAll();

        return cards.stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    public CardResponseDTO buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID nao pode ser nulo");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card nao encontrado com id: " + id));

        return converterParaResponseDTO(card);
    }

    public CardResponseDTO atualizarCard(Long id, CardRequestDTO requestDTO) {
        if (id == null) {
            throw new ResourceNotFoundException("ID nao pode ser nulo");
        }

        validarRegrasDeNegocio(requestDTO);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card nao encontrado com id: " + id));

        if (card != null) {
            preencherCard(card, requestDTO);

            Card cardAtualizado = cardRepository.save(card);
            return converterParaResponseDTO(cardAtualizado);
        }
        
        throw new ResourceNotFoundException("Card nao encontrado com id: " + id);
    }

    public void deletarCard(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID nao pode ser nulo");
        }

        cardRepository.deleteById(id);
    }

    private void preencherCard(Card card, CardRequestDTO requestDTO) {
        card.setTitulo(requestDTO.getTitulo().trim());
        card.setDescricao(limparTexto(requestDTO.getDescricao()));
        card.setCategoria(requestDTO.getCategoria());
        card.setPrioridade(requestDTO.getPrioridade());
        card.setDataEvento(requestDTO.getDataEvento());
        card.setResponsavel(limparTexto(requestDTO.getResponsavel()));
        card.setStatus(requestDTO.getStatus());
        card.setObservacoes(limparTexto(requestDTO.getObservacoes()));
    }

    private void validarRegrasDeNegocio(CardRequestDTO requestDTO) {
        CategoriaCard categoria = requestDTO.getCategoria();

        if (categoria == null) {
            return;
        }

        if (exigeDataEvento(categoria) && requestDTO.getDataEvento() == null) {
            throw new IllegalArgumentException("Informe a data para este tipo de card.");
        }

        if (exigeResponsavel(categoria) && !StringUtils.hasText(requestDTO.getResponsavel())) {
            throw new IllegalArgumentException("Informe o responsavel para este tipo de card.");
        }

        if (categoria == CategoriaCard.AVISO_NOTA && !StringUtils.hasText(requestDTO.getDescricao())) {
            throw new IllegalArgumentException("Avisos e notas precisam de uma descricao.");
        }
    }

    private boolean exigeDataEvento(CategoriaCard categoria) {
        return categoria == CategoriaCard.EVENTO
                || categoria == CategoriaCard.FALTA_PROFESSOR
                || categoria == CategoriaCard.HORARIO_PROFESSOR;
    }

    private boolean exigeResponsavel(CategoriaCard categoria) {
        return categoria == CategoriaCard.EVENTO
                || categoria == CategoriaCard.FALTA_PROFESSOR
                || categoria == CategoriaCard.HORARIO_PROFESSOR;
    }

    private String limparTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }

    private CardResponseDTO converterParaResponseDTO(Card card) {
        CardResponseDTO responseDTO = new CardResponseDTO();

        responseDTO.setId(card.getId());
        responseDTO.setTitulo(card.getTitulo());
        responseDTO.setDescricao(card.getDescricao());
        responseDTO.setCategoria(card.getCategoria());
        responseDTO.setPrioridade(card.getPrioridade());
        responseDTO.setDataCriacao(card.getDataCriacao());
        responseDTO.setDataEvento(card.getDataEvento());
        responseDTO.setResponsavel(card.getResponsavel());
        responseDTO.setStatus(card.getStatus());
        responseDTO.setObservacoes(card.getObservacoes());

        return responseDTO;
    }
}

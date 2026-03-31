package br.com.escola.dashboard.repository;

// Importa a entidade Card (que representa a tabela no banco)
import br.com.escola.dashboard.entity.Card;

// Importa o JpaRepository do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

// Importa anotações opcionais (caso queira usar queries customizadas depois)
import org.springframework.stereotype.Repository;

/**
 * 🔹 CardRepository
 * 
 * Essa interface é responsável por fazer a comunicação com o banco de dados.
 * Ela permite salvar, buscar, atualizar e deletar dados da entidade Card.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    

    // 🚀 IMPORTANTE:
    // Você NÃO precisa implementar nada aqui para o básico funcionar!
    // O Spring já fornece métodos prontos automaticamente.

    /**
     * Métodos que você já tem automaticamente:
     * 
     * save(card)           -> salva ou atualiza um card
     * findAll()            -> retorna todos os cards
     * findById(id)         -> busca um card por ID
     * deleteById(id)       -> deleta um card por ID
     */

    // 🔹 Exemplo de método customizado (opcional)
    // O Spring cria automaticamente baseado no nome

    // Listar cards por título
    // List<Card> findByTitulo(String titulo);

    // Listar cards por categoria
    // List<Card> findByCategoria(CategoriaCard categoria);

}
package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Card;
import br.com.escola.dashboard.enums.CategoriaCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByCategoria(CategoriaCard categoria);

    List<Card> findByCategoria(CategoriaCard categoria);

    List<Card> findByDataEventoOrderByTituloAsc(LocalDate dataEvento);
}

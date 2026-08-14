package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plus.domain.Deck;

public interface DeckRepository extends JdbcRepository<Deck, Long>, DeckRepositoryCustom {
}

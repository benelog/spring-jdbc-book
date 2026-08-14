package flashcard.plusdeep.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plusdeep.domain.Deck;

// tag::class[]
public interface DeckRepository extends JdbcRepository<Deck, Long>, DeckRepositoryCustom {
}
// end::class[]

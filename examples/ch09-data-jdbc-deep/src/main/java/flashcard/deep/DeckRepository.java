package flashcard.deep;

import org.springframework.data.repository.ListCrudRepository;

public interface DeckRepository extends ListCrudRepository<Deck, Long> {
}

package flashcard.boot;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public long createDeck(String name) {
        return deckRepository.insert(name);
    }

    @Transactional
    public long addCard(long deckId, String text, String meaning) {
        return cardRepository.insert(Card.of(deckId, text, meaning));
    }

    @Transactional
    public void deleteCard(long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional
    public void deleteDeck(long deckId) {
        // cards는 on delete cascade로 함께 지워진다
        deckRepository.deleteById(deckId);
    }

    @Transactional(readOnly = true)
    public List<DeckSummary> deckSummaries() {
        return deckRepository.findAllSummaries();
    }

    @Transactional(readOnly = true)
    public Deck getDeck(long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("덱이 없습니다: " + deckId));
    }

    @Transactional(readOnly = true)
    public List<Card> cardsOf(long deckId) {
        return cardRepository.findByDeckId(deckId);
    }
}

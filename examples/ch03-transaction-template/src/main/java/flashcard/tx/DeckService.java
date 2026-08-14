package flashcard.tx;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * TransactionTemplate으로 트랜잭션 경계를 감싼 서비스.
 * try-catch-finally와 commit/rollback 호출이 모두 사라졌다.
 */
public class DeckService {

    // tag::init[]
    private final TransactionTemplate transaction;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeckService(PlatformTransactionManager transactionManager,
                       DeckRepository deckRepository, CardRepository cardRepository) {
        this.transaction = new TransactionTemplate(transactionManager);
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }
    // end::init[]

    // tag::execute[]
    /** 덱과 카드를 한 트랜잭션으로 저장하고, 새 덱의 id를 돌려준다. */
    public long importDeck(String deckName, List<Card> cards) {
        return transaction.execute(status -> {
            long deckId = deckRepository.insert(deckName);
            List<Card> deckCards = cards.stream()
                    .map(card -> Card.of(deckId, card.text(), card.meaning()))
                    .toList();
            cardRepository.insertAll(deckCards);
            return deckId;
        });
    }
    // end::execute[]

    // tag::without-result[]
    /** 반환값이 필요 없으면 executeWithoutResult를 쓴다. */
    public void mergeDecks(long fromDeckId, long toDeckId) {
        transaction.executeWithoutResult(status -> {
            cardRepository.moveToDeck(fromDeckId, toDeckId);
            deckRepository.deleteById(fromDeckId);
        });
    }
    // end::without-result[]
}

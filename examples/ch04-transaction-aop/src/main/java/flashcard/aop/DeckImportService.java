package flashcard.aop;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 가져오기 완료를 이벤트로 알리는 DeckService의 변형. */
// tag::publish[]
@Service
public class DeckImportService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeckImportService(DeckRepository deckRepository, CardRepository cardRepository,
            ApplicationEventPublisher eventPublisher) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public long importDeck(String deckName, List<Card> cards) {
        long deckId = deckRepository.insert(deckName);
        // 발행은 즉시 일어나지만, 리스너 실행은 커밋 후로 미뤄진다
        eventPublisher.publishEvent(new DeckImported(deckId, deckName, cards.size()));
        for (Card card : cards) {
            cardRepository.insert(Card.of(deckId, card.text(), card.meaning()));
        }
        return deckId;
    }
}
// end::publish[]

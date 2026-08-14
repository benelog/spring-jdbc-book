package flashcard.boot;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

// tag::jdbc-test[]
@JdbcTest
@Import({DeckRepository.class, CardRepository.class})
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;

    @Test
    void 덱_요약에_카드_수가_함께_나온다() {
        long deckId = deckRepository.insert("영어 단어장");
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertEquals(1, summaries.size());
        assertEquals("영어 단어장", summaries.getFirst().name());
        assertEquals(2, summaries.getFirst().cardCount());
    }
    // end::jdbc-test[]

    @Test
    void 카드가_없는_덱은_카드_수가_0이다() {
        deckRepository.insert("빈 덱");

        assertEquals(0, deckRepository.findAllSummaries().getFirst().cardCount());
    }

    @Test
    void 덱을_지우면_카드도_함께_지워진다() {
        long deckId = deckRepository.insert("영어 단어장");
        long cardId = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));

        deckRepository.deleteById(deckId);

        assertEquals(true, cardRepository.findById(cardId).isEmpty());
    }
}

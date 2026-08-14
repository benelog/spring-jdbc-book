package flashcard.plusdeep.repository;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plusdeep.domain.Card;
import flashcard.plusdeep.domain.Deck;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-deep-dao;DB_CLOSE_DELAY=-1"
})
@Transactional
class DeckStatsDaoTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckStatsDao deckStatsDao;

    // tag::dao[]
    @Test
    @DisplayName("휴지통을 제외한 덱별 카드 수를 집계한다")
    void countCardsPerDeck() {
        Long deckId = deckRepository.insert(Deck.create("영어 단어장")).id();
        cardRepository.insert(new Card(deckId, "resilient", "회복력 있는"));
        cardRepository.insert(new Card(deckId, "deliberate", "의도적인"));
        Deck trash = deckRepository.insert(Deck.create("지운 단어장"));
        deckRepository.delete(trash);

        List<DeckCardCount> counts = deckStatsDao.countCardsPerDeck();

        assertThat(counts).containsExactly(new DeckCardCount("영어 단어장", 2));
    }
    // end::dao[]
}

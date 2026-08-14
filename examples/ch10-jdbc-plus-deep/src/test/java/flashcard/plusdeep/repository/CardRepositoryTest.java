package flashcard.plusdeep.repository;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plusdeep.domain.Card;
import flashcard.plusdeep.domain.CardExample;
import flashcard.plusdeep.domain.Deck;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-deep-card;DB_CLOSE_DELAY=-1"
})
@Transactional
class CardRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;

    Long deckId;

    @BeforeEach
    void setUp() {
        deckId = deckRepository.insert(Deck.create("영어 단어장")).id();
    }

    // tag::aggregate[]
    @Test
    @DisplayName("검색 결과 애그리거트가 예문까지 쿼리 한 번으로 조립된다")
    void searchWithExamples() {
        Card card = new Card(deckId, "resilient", "회복력 있는");
        card.addExample("She is resilient.", "그는 회복력이 있다.");
        card.addExample("A resilient economy.", "회복력 있는 경제.");
        cardRepository.insert(card);
        cardRepository.insert(new Card(deckId, "deliberate", "의도적인"));

        List<Card> found = cardRepository.searchWithExamples("resil");

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getExamples())
                .extracting(CardExample::sentence)
                .containsExactly("She is resilient.", "A resilient economy.");
    }
    // end::aggregate[]

    // tag::batch[]
    @Test
    @DisplayName("카드 100장을 배치 INSERT 한 번으로 저장한다")
    void insertBatch() {
        List<Card> cards = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new Card(deckId, "word%03d".formatted(i), "뜻" + i))
                .toList();

        int[] results = cardRepository.insertBatch(cards);

        assertThat(results).hasSize(100);
        assertThat(cardRepository.count()).isEqualTo(100);
    }
    // end::batch[]
}

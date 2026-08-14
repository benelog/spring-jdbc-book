package flashcard.boot;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

// tag::jdbc-test[]
@JdbcTest
@Import({DeckRepository.class, CardRepository.class})
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;

    @Test
    @DisplayName("덱 요약에 카드 수가 함께 나온다")
    void summariesWithCardCount() {
        long deckId = deckRepository.insert("영어 단어장");
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.getFirst().name()).isEqualTo("영어 단어장");
        assertThat(summaries.getFirst().cardCount()).isEqualTo(2);
    }
    // end::jdbc-test[]

    @Test
    @DisplayName("카드가 없는 덱은 카드 수가 0이다")
    void emptyDeckCardCount() {
        deckRepository.insert("빈 덱");

        assertThat(deckRepository.findAllSummaries().getFirst().cardCount()).isZero();
    }

    // tag::sql[]
    @Test
    @DisplayName("@Sql로 준비한 데이터를 조회한다")
    @Sql(statements = {
            "insert into decks (id, name) values (10, '영어 단어장')",
            "insert into cards (deck_id, text, meaning) values (10, 'resilient', '회복력 있는')"
    })
    void summariesFromSqlData() {
        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertThat(summaries.getFirst().cardCount()).isEqualTo(1);
    }
    // end::sql[]

    @Test
    @DisplayName("덱을 지우면 카드도 함께 지워진다")
    void deleteCascades() {
        long deckId = deckRepository.insert("영어 단어장");
        long cardId = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));

        deckRepository.deleteById(deckId);

        assertThat(cardRepository.findById(cardId)).isEmpty();
    }
}

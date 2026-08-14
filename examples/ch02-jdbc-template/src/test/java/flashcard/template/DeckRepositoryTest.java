package flashcard.template;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;

class DeckRepositoryTest {

    EmbeddedDatabase dataSource;
    DeckRepository deckRepository;
    CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .build();
        deckRepository = new DeckRepository(dataSource);
        cardRepository = new CardRepository(dataSource);
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }

    @Test
    @DisplayName("이름이 같은 덱이 있으면 기존 덱의 id를 돌려준다")
    void findOrCreate() {
        long first = deckRepository.findOrCreate("영어 단어장");
        long second = deckRepository.findOrCreate("영어 단어장");

        assertThat(second).isEqualTo(first);
        assertThat(deckRepository.countAll()).isEqualTo(1);
    }

    @Test
    @DisplayName("덱과 카드를 쿼리 한 번으로 조회한다")
    void findWithCards() {
        long deckId = deckRepository.insert("영어 단어장");
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        DeckWithCards deck = deckRepository.findWithCards(deckId).orElseThrow();

        assertThat(deck.name()).isEqualTo("영어 단어장");
        assertThat(deck.cards()).hasSize(2);
        assertThat(deck.cards().getFirst().text()).isEqualTo("resilient");
    }

    @Test
    @DisplayName("카드가 없는 덱은 빈 목록으로 조립된다")
    void findWithCardsEmpty() {
        long deckId = deckRepository.insert("빈 덱");

        DeckWithCards deck = deckRepository.findWithCards(deckId).orElseThrow();

        assertThat(deck.cards()).isEmpty();
    }

    @Test
    @DisplayName("없는 덱은 빈 Optional을 돌려준다")
    void findWithCardsMissing() {
        assertThat(deckRepository.findWithCards(999L)).isEmpty();
    }
}

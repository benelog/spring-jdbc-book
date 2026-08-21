package flashcard.tx;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeckServiceTest {

    EmbeddedDatabase dataSource;
    DeckRepository deckRepository;
    CardRepository cardRepository;
    DeckService deckService;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .build();
        deckRepository = new DeckRepository(dataSource);
        cardRepository = new CardRepository(dataSource);
        deckService = new DeckService(
                new JdbcTransactionManager(dataSource), deckRepository, cardRepository);
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }

    @Test
    @DisplayName("덱과 카드를 한 트랜잭션으로 저장한다")
    void importDeck() {
        long deckId = deckService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "deliberate", "의도적인")
        ));

        assertThat(cardRepository.findByDeckId(deckId)).hasSize(2);
    }

    // tag::rollback[]
    @Test
    @DisplayName("카드 저장이 실패하면 덱까지 함께 롤백된다")
    void rollbackOnFailure() {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "x".repeat(600), "칼럼 길이 제한 초과")
        );

        assertThatThrownBy(() -> deckService.importDeck("영어 단어장", cards))
                .isInstanceOf(DataAccessException.class);

        assertThat(deckRepository.countAll()).isZero();
        assertThat(cardRepository.countAll()).isZero();
    }
    // end::rollback[]

    @Test
    @DisplayName("덱을 합치면 카드가 옮겨지고 원래 덱은 지워진다")
    void mergeDecks() {
        long fromDeck = deckService.importDeck("단어장 A", List.of(Card.of(null, "a", "에이")));
        long toDeck = deckService.importDeck("단어장 B", List.of(Card.of(null, "b", "비")));

        deckService.mergeDecks(fromDeck, toDeck);

        assertThat(cardRepository.findByDeckId(toDeck)).hasSize(2);
        assertThat(deckRepository.countAll()).isEqualTo(1);
    }
}

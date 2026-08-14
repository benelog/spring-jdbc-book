package flashcard.tx;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void 덱과_카드를_한_트랜잭션으로_저장한다() {
        long deckId = deckService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "deliberate", "의도적인")
        ));

        assertEquals(2, cardRepository.findByDeckId(deckId).size());
    }

    // tag::rollback[]
    @Test
    void 카드_저장이_실패하면_덱까지_함께_롤백된다() {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "x".repeat(600), "컬럼 길이 제한 초과")
        );

        assertThrows(DataAccessException.class,
                () -> deckService.importDeck("영어 단어장", cards));

        assertEquals(0, deckRepository.countAll());
        assertEquals(0, cardRepository.countAll());
    }
    // end::rollback[]

    @Test
    void 덱을_합치면_카드가_옮겨지고_원래_덱은_지워진다() {
        long fromDeck = deckService.importDeck("단어장 A", List.of(Card.of(null, "a", "에이")));
        long toDeck = deckService.importDeck("단어장 B", List.of(Card.of(null, "b", "비")));

        deckService.mergeDecks(fromDeck, toDeck);

        assertEquals(2, cardRepository.findByDeckId(toDeck).size());
        assertEquals(1, deckRepository.countAll());
    }
}

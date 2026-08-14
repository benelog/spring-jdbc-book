package flashcard.jdbc;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckImporterTest {

    DataSource dataSource;
    DeckImporter importer;
    DeckDao deckDao;
    CardDao cardDao;

    @BeforeEach
    void setUp() {
        dataSource = TestDb.newInMemoryDb("deck-importer-test");
        importer = new DeckImporter(dataSource);
        deckDao = new DeckDao(dataSource);
        cardDao = new CardDao(dataSource);
    }

    @Test
    void 덱과_카드를_한_트랜잭션으로_저장한다() throws SQLException {
        long deckId = importer.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "deliberate", "의도적인")
        ));

        assertEquals(2, cardDao.findByDeckId(deckId).size());
    }

    // tag::rollback[]
    @Test
    void 카드_저장이_실패하면_덱도_함께_되돌린다() throws SQLException {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "too long".repeat(100), "컬럼 길이 제한 초과")  // 500자 초과 → 실패
        );

        assertThrows(SQLException.class, () -> importer.importDeck("영어 단어장", cards));

        assertTrue(deckDao.findAll().isEmpty());  // 덱 insert도 롤백됐다
    }
    // end::rollback[]
}

package flashcard.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardDaoTest {

    DataSource dataSource;
    DeckDao deckDao;
    CardDao cardDao;
    long deckId;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = TestDb.newInMemoryDb("card-dao-test");
        deckDao = new DeckDao(dataSource);
        cardDao = new CardDao(dataSource);
        deckId = deckDao.insert("영어 단어장");
    }

    // tag::crud[]
    @Test
    void 카드를_저장하고_조회한다() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));

        Optional<Card> found = cardDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("resilient", found.get().text());
        assertEquals("회복력 있는", found.get().meaning());
    }
    // end::crud[]

    @Test
    void 덱의_카드를_모두_조회한다() throws SQLException {
        cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));
        cardDao.insert(Card.of(deckId, "deliberate", "의도적인"));

        List<Card> cards = cardDao.findByDeckId(deckId);

        assertEquals(2, cards.size());
    }

    @Test
    void 카드를_수정한다() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력"));

        int updated = cardDao.update(new Card(id, deckId, "resilient", "회복력 있는, 잘 튀어오르는"));

        assertEquals(1, updated);
        assertEquals("회복력 있는, 잘 튀어오르는", cardDao.findById(id).orElseThrow().meaning());
    }

    @Test
    void 카드를_삭제한다() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));

        assertEquals(1, cardDao.deleteById(id));
        assertTrue(cardDao.findById(id).isEmpty());
    }

    // tag::batch[]
    @Test
    void 여러_카드를_배치로_저장한다() throws SQLException {
        List<Card> cards = List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인"),
                Card.of(deckId, "profound", "심오한")
        );

        int[] results = cardDao.insertAll(deckId, cards);

        assertEquals(3, results.length);
        assertEquals(3, cardDao.findByDeckId(deckId).size());
    }
    // end::batch[]
}

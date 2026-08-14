package flashcard.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("카드를 저장하고 조회한다")
    void insertAndFind() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));

        Optional<Card> found = cardDao.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().text()).isEqualTo("resilient");
        assertThat(found.get().meaning()).isEqualTo("회복력 있는");
    }
    // end::crud[]

    @Test
    @DisplayName("덱의 카드를 모두 조회한다")
    void findByDeckId() throws SQLException {
        cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));
        cardDao.insert(Card.of(deckId, "deliberate", "의도적인"));

        List<Card> cards = cardDao.findByDeckId(deckId);

        assertThat(cards).hasSize(2);
    }

    @Test
    @DisplayName("카드를 수정한다")
    void update() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력"));

        int updated = cardDao.update(new Card(id, deckId, "resilient", "회복력 있는, 잘 튀어오르는"));

        assertThat(updated).isEqualTo(1);
        assertThat(cardDao.findById(id).orElseThrow().meaning()).isEqualTo("회복력 있는, 잘 튀어오르는");
    }

    @Test
    @DisplayName("카드를 삭제한다")
    void deleteById() throws SQLException {
        long id = cardDao.insert(Card.of(deckId, "resilient", "회복력 있는"));

        assertThat(cardDao.deleteById(id)).isEqualTo(1);
        assertThat(cardDao.findById(id)).isEmpty();
    }

    // tag::batch[]
    @Test
    @DisplayName("여러 카드를 배치로 저장한다")
    void insertAll() throws SQLException {
        List<Card> cards = List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인"),
                Card.of(deckId, "profound", "심오한")
        );

        int[] results = cardDao.insertAll(deckId, cards);

        assertThat(results).hasSize(3);
        assertThat(cardDao.findByDeckId(deckId)).hasSize(3);
    }
    // end::batch[]
}

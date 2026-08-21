package flashcard.jdbc;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("덱과 카드를 한 트랜잭션으로 저장한다")
    void importDeck() throws SQLException {
        long deckId = importer.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "deliberate", "의도적인")
        ));

        assertThat(cardDao.findByDeckId(deckId)).hasSize(2);
    }

    // tag::rollback[]
    @Test
    @DisplayName("카드 저장이 실패하면 덱도 함께 되돌린다")
    void rollbackOnFailure() throws SQLException {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "too long".repeat(100), "칼럼 길이 제한 초과")  // 500자 초과 → 실패
        );

        assertThatThrownBy(() -> importer.importDeck("영어 단어장", cards))
                .isInstanceOf(SQLException.class);

        assertThat(deckDao.findAll()).isEmpty();  // 덱 insert도 롤백됐다
    }
    // end::rollback[]
}

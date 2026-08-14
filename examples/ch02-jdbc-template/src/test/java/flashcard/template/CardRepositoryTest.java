package flashcard.template;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardRepositoryTest {

    EmbeddedDatabase dataSource;
    DeckRepository deckRepository;
    CardRepository cardRepository;
    long deckId;

    // tag::embedded[]
    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .build();
        // end::embedded[]
        deckRepository = new DeckRepository(dataSource);
        cardRepository = new CardRepository(dataSource);
        deckId = deckRepository.insert("영어 단어장");
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }

    @Test
    void 카드를_저장하고_조회한다() {
        long id = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));

        Card found = cardRepository.findById(id).orElseThrow();

        assertEquals("resilient", found.text());
        assertEquals(deckId, found.deckId());
    }

    @Test
    void 없는_카드는_빈_Optional을_돌려준다() {
        assertTrue(cardRepository.findById(999L).isEmpty());
    }

    @Test
    void 여러_id로_한번에_조회한다() {
        long id1 = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));
        long id2 = cardRepository.insert(Card.of(deckId, "deliberate", "의도적인"));
        cardRepository.insert(Card.of(deckId, "profound", "심오한"));

        List<Card> cards = cardRepository.findByIds(List.of(id1, id2));

        assertEquals(2, cards.size());
    }

    @Test
    void 배치로_저장한다() {
        int[] results = cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        assertEquals(2, results.length);
        assertEquals(2, cardRepository.countByDeckId(deckId));
    }

    @Test
    void 카드를_수정한다() {
        long id = cardRepository.insert(Card.of(deckId, "resilient", "회복력"));

        cardRepository.update(new Card(id, deckId, "resilient", "회복력 있는"));

        assertEquals("회복력 있는", cardRepository.findById(id).orElseThrow().meaning());
    }

    // tag::search[]
    @Test
    void 키워드가_있으면_원문과_뜻에서_검색한다() {
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인"),
                Card.of(deckId, "recover", "회복하다")
        ));

        List<Card> found = cardRepository.search(deckId, "회복");

        assertEquals(2, found.size());
    }

    @Test
    void 조건이_없으면_전체를_조회한다() {
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        assertEquals(2, cardRepository.search(null, null).size());
    }
    // end::search[]
}

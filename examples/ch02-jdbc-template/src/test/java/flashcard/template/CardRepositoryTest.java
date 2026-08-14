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
    @DisplayName("카드를 저장하고 조회한다")
    void insertAndFind() {
        long id = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));

        Card found = cardRepository.findById(id).orElseThrow();

        assertThat(found.text()).isEqualTo("resilient");
        assertThat(found.deckId()).isEqualTo(deckId);
    }

    @Test
    @DisplayName("SimpleJdbcInsert로 SQL 없이 저장한다")
    void insertWithSimpleJdbcInsert() {
        long id = cardRepository.insertWithSimpleJdbcInsert(Card.of(deckId, "deliberate", "의도적인"));

        Card found = cardRepository.findById(id).orElseThrow();

        assertThat(found.text()).isEqualTo("deliberate");
        assertThat(found.deckId()).isEqualTo(deckId);
    }

    @Test
    @DisplayName("없는 카드는 빈 Optional을 돌려준다")
    void findMissing() {
        assertThat(cardRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("여러 id로 한번에 조회한다")
    void findByIds() {
        long id1 = cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));
        long id2 = cardRepository.insert(Card.of(deckId, "deliberate", "의도적인"));
        cardRepository.insert(Card.of(deckId, "profound", "심오한"));

        List<Card> cards = cardRepository.findByIds(List.of(id1, id2));

        assertThat(cards).hasSize(2);
    }

    @Test
    @DisplayName("배치로 저장한다")
    void insertAll() {
        int[] results = cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        assertThat(results).hasSize(2);
        assertThat(cardRepository.countByDeckId(deckId)).isEqualTo(2);
    }

    @Test
    @DisplayName("카드를 수정한다")
    void update() {
        long id = cardRepository.insert(Card.of(deckId, "resilient", "회복력"));

        cardRepository.update(new Card(id, deckId, "resilient", "회복력 있는"));

        assertThat(cardRepository.findById(id).orElseThrow().meaning()).isEqualTo("회복력 있는");
    }

    // tag::search[]
    @Test
    @DisplayName("키워드가 있으면 원문과 뜻에서 검색한다")
    void searchByKeyword() {
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인"),
                Card.of(deckId, "recover", "회복하다")
        ));

        List<Card> found = cardRepository.search(deckId, "회복");

        assertThat(found).hasSize(2);
    }

    @Test
    @DisplayName("조건이 없으면 전체를 조회한다")
    void searchWithoutCondition() {
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        assertThat(cardRepository.search(null, null)).hasSize(2);
    }
    // end::search[]
}

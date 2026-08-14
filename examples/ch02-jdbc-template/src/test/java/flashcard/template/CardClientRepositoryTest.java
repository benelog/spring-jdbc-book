package flashcard.template;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;

class CardClientRepositoryTest {

    EmbeddedDatabase dataSource;
    DeckRepository deckRepository;
    CardClientRepository cardRepository;
    long deckId;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .build();
        deckRepository = new DeckRepository(dataSource);
        cardRepository = new CardClientRepository(dataSource);
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
    @DisplayName("없는 카드는 빈 Optional을 돌려준다")
    void findMissing() {
        assertThat(cardRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("덱의 카드를 조회하고 수를 센다")
    void findAndCount() {
        cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));
        cardRepository.insert(Card.of(deckId, "deliberate", "의도적인"));

        assertThat(cardRepository.findByDeckId(deckId)).hasSize(2);
        assertThat(cardRepository.countByDeckId(deckId)).isEqualTo(2);
    }

    @Test
    @DisplayName("물음표 파라미터로 검색한다")
    void findByKeyword() {
        cardRepository.insert(Card.of(deckId, "resilient", "회복력 있는"));
        cardRepository.insert(Card.of(deckId, "recover", "회복하다"));

        assertThat(cardRepository.findByKeyword("re")).hasSize(2);
    }

    @Test
    @DisplayName("카드를 수정한다")
    void update() {
        long id = cardRepository.insert(Card.of(deckId, "resilient", "회복력"));

        int affected = cardRepository.update(new Card(id, deckId, "resilient", "회복력 있는"));

        assertThat(affected).isEqualTo(1);
        assertThat(cardRepository.findById(id).orElseThrow().meaning()).isEqualTo("회복력 있는");
    }
}

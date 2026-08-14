package flashcard.template;

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

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("schema.sql")
                .build();
        deckRepository = new DeckRepository(dataSource);
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }

    @Test
    @DisplayName("덱을 저장하고 조회한다")
    void insertAndFind() {
        long id = deckRepository.insert("영어 단어장");

        assertThat(deckRepository.countAll()).isEqualTo(1);
        assertThat(deckRepository.findAll().getFirst().name()).isEqualTo("영어 단어장");
        assertThat(deckRepository.findAll().getFirst().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("덱 이름을 바꾼다")
    void rename() {
        long id = deckRepository.insert("영어 단어장");

        deckRepository.rename(id, "TOEIC 단어장");

        assertThat(deckRepository.findAll().getFirst().name()).isEqualTo("TOEIC 단어장");
    }
}

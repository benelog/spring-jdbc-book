package flashcard.template;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void 덱을_저장하고_조회한다() {
        long id = deckRepository.insert("영어 단어장");

        assertEquals(1, deckRepository.countAll());
        assertEquals("영어 단어장", deckRepository.findAll().getFirst().name());
        assertEquals(id, deckRepository.findAll().getFirst().id());
    }

    @Test
    void 덱_이름을_바꾼다() {
        long id = deckRepository.insert("영어 단어장");

        deckRepository.rename(id, "TOEIC 단어장");

        assertEquals("TOEIC 단어장", deckRepository.findAll().getFirst().name());
    }
}

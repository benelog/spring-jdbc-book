package flashcard.aop;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// tag::config[]
@SpringJUnitConfig(DeckServiceTest.TestConfig.class)
class DeckServiceTest {

    @Configuration
    @Import(AppConfig.class)
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .generateUniqueName(true)
                    .addScript("schema.sql")
                    .build();
        }
    }
    // end::config[]

    @Autowired
    DeckService deckService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DataSource dataSource;

    @AfterEach
    void cleanUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("delete from cards");
        jdbc.update("delete from decks");
    }

    @Test
    void 프록시가_주입된다() {
        assertTrue(deckService.getClass().getName().contains("$$"));
    }

    @Test
    void 덱과_카드를_한_트랜잭션으로_저장한다() {
        long deckId = deckService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는")
        ));

        assertEquals(1, deckService.cardsOf(deckId).size());
    }

    @Test
    void 런타임_예외가_나면_전체가_롤백된다() {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "x".repeat(600), "컬럼 길이 초과")
        );

        assertThrows(DataAccessException.class,
                () -> deckService.importDeck("영어 단어장", cards));

        assertEquals(0, deckRepository.countAll());
        assertEquals(0, cardRepository.countAll());
    }

    // tag::checked-pitfall[]
    @Test
    void checked_예외는_기본적으로_롤백되지_않는다() {
        String csv = """
                resilient,회복력 있는
                이_행은_쉼표가_없다
                """;

        assertThrows(CsvFormatException.class,
                () -> deckService.importCsv("영어 단어장", csv));

        // 예외가 났는데도 덱과 첫 카드는 커밋되어 있다!
        assertEquals(1, deckRepository.countAll());
        assertEquals(1, cardRepository.countAll());
    }

    @Test
    void rollbackFor를_지정하면_checked_예외에도_롤백된다() {
        String csv = """
                resilient,회복력 있는
                이_행은_쉼표가_없다
                """;

        assertThrows(CsvFormatException.class,
                () -> deckService.importCsvStrictly("영어 단어장", csv));

        assertEquals(0, deckRepository.countAll());
        assertEquals(0, cardRepository.countAll());
    }
    // end::checked-pitfall[]
}

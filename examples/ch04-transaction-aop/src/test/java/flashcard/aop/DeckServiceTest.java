package flashcard.aop;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    // tag::cleanup[]
    @AfterEach
    void cleanUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("delete from cards");
        jdbc.update("delete from decks");
    }
    // end::cleanup[]

    @Test
    @DisplayName("프록시가 주입된다")
    void proxyInjected() {
        assertThat(deckService.getClass().getName()).contains("$$");
    }

    @Test
    @DisplayName("덱과 카드를 한 트랜잭션으로 저장한다")
    void importDeck() {
        long deckId = deckService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는")
        ));

        assertThat(deckService.cardsOf(deckId)).hasSize(1);
    }

    @Test
    @DisplayName("런타임 예외가 나면 전체가 롤백된다")
    void rollbackOnRuntimeException() {
        List<Card> cards = List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "x".repeat(600), "칼럼 길이 초과")
        );

        assertThatThrownBy(() -> deckService.importDeck("영어 단어장", cards))
                .isInstanceOf(DataAccessException.class);

        assertThat(deckRepository.countAll()).isZero();
        assertThat(cardRepository.countAll()).isZero();
    }

    // tag::checked-pitfall[]
    @Test
    @DisplayName("checked 예외는 기본적으로 롤백되지 않는다")
    void noRollbackOnCheckedException() {
        String csv = """
                resilient,회복력 있는
                이_행은_쉼표가_없다
                """;

        assertThatThrownBy(() -> deckService.importCsv("영어 단어장", csv))
                .isInstanceOf(CsvFormatException.class);

        // 예외가 났는데도 덱과 첫 카드는 커밋되어 있다!
        assertThat(deckRepository.countAll()).isEqualTo(1);
        assertThat(cardRepository.countAll()).isEqualTo(1);
    }

    @Test
    @DisplayName("rollbackFor를 지정하면 checked 예외에도 롤백된다")
    void rollbackForCheckedException() {
        String csv = """
                resilient,회복력 있는
                이_행은_쉼표가_없다
                """;

        assertThatThrownBy(() -> deckService.importCsvStrictly("영어 단어장", csv))
                .isInstanceOf(CsvFormatException.class);

        assertThat(deckRepository.countAll()).isZero();
        assertThat(cardRepository.countAll()).isZero();
    }
    // end::checked-pitfall[]
}

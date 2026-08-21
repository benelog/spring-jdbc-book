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

@SpringJUnitConfig(DeckImportServiceTest.TestConfig.class)
class DeckImportServiceTest {

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

    @Autowired
    DeckImportService deckImportService;
    @Autowired
    DeckImportNotifier notifier;
    @Autowired
    DataSource dataSource;

    @AfterEach
    void cleanUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("delete from cards");
        jdbc.update("delete from decks");
        notifier.clear();
    }

    // tag::test[]
    @Test
    @DisplayName("커밋이 끝난 뒤에 알림이 발송된다")
    void notifyAfterCommit() {
        deckImportService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는")
        ));

        assertThat(notifier.sentMessages())
                .containsExactly("덱 '영어 단어장'에 카드 1장이 등록되었습니다");
    }

    @Test
    @DisplayName("롤백되면 알림이 발송되지 않는다")
    void noNotificationOnRollback() {
        List<Card> cards = List.of(
                Card.of(null, "x".repeat(600), "칼럼 길이 초과")
        );

        assertThatThrownBy(() -> deckImportService.importDeck("영어 단어장", cards))
                .isInstanceOf(DataAccessException.class);

        assertThat(notifier.sentMessages()).isEmpty();
    }
    // end::test[]
}

package flashcard.datajdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.BadSqlGrammarException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** PostgreSQL: 따옴표 없는 DDL이 소문자로 저장되므로 대문자 이름은 테이블을 찾지 못한다. */
// tag::class[]
@Testcontainers(disabledWithoutDocker = true)
@DataJdbcTest
class IdentifierCasePostgresTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    LowerDeckRepository lowerDeckRepository;

    @Test
    @DisplayName("대문자 @Table(\"DECKS\")는 PostgreSQL에서 테이블을 찾지 못한다")
    void upperCaseFails() {
        assertThatThrownBy(() -> deckRepository.save(new Deck("영어 단어장")))
                .isInstanceOf(BadSqlGrammarException.class);
    }

    @Test
    @DisplayName("소문자 @Table(\"decks\")는 PostgreSQL에서 동작한다")
    void lowerCaseWorks() {
        assertThat(lowerDeckRepository.save(new LowerDeck(null, "영어 단어장")).id()).isNotNull();
    }
}
// end::class[]

package flashcard.datajdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;

import static org.assertj.core.api.Assertions.assertThat;

/** CASE_INSENSITIVE_IDENTIFIERS=TRUE를 붙인 H2: 따옴표로 감싼 식별자도 대소문자를 구별하지 않는다. */
// tag::class[]
@DataJdbcTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:h2:mem:case-insensitive;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1"
})
class IdentifierCaseInsensitiveH2Test {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    LowerDeckRepository lowerDeckRepository;

    @Test
    @DisplayName("대문자 이름도 동작한다")
    void upperCaseWorks() {
        assertThat(deckRepository.save(new Deck("영어 단어장")).getId()).isNotNull();
    }

    @Test
    @DisplayName("소문자 이름도 동작한다")
    void lowerCaseWorks() {
        assertThat(lowerDeckRepository.save(new LowerDeck(null, "영어 단어장")).id()).isNotNull();
    }
}
// end::class[]

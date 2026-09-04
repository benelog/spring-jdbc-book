package flashcard.datajdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.jdbc.BadSqlGrammarException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** 기본 설정의 H2: 따옴표 없는 DDL이 대문자로 저장되므로 대문자 이름만 통한다. */
// tag::class[]
@DataJdbcTest
class IdentifierCaseH2Test {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    LowerDeckRepository lowerDeckRepository;

    @Test
    @DisplayName("대문자 @Table(\"DECKS\")는 H2에서 동작한다")
    void upperCaseWorks() {
        Deck saved = deckRepository.save(new Deck("영어 단어장"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("소문자 @Table(\"decks\")는 H2에서 테이블을 찾지 못한다")
    void lowerCaseFails() {
        assertThatThrownBy(() -> lowerDeckRepository.save(new LowerDeck(null, "영어 단어장")))
                .isInstanceOf(BadSqlGrammarException.class);
    }
}
// end::class[]

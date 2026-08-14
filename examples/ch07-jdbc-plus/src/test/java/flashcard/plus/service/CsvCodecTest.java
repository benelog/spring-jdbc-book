package flashcard.plus.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.CardWithTags;
import flashcard.plus.service.CsvCodec.CsvCard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvCodecTest {

    @Test
    @DisplayName("원문과 뜻, 태그를 읽는다")
    void parse() {
        String csv = """
                resilient,회복력 있는,형용사;TOEIC
                deliberate,의도적인
                """;

        List<CsvCard> cards = CsvCodec.parse(csv);

        assertThat(cards).hasSize(2);
        assertThat(cards.get(0).tags()).containsExactly("형용사", "TOEIC");
        assertThat(cards.get(1).tags()).isEmpty();
    }

    @Test
    @DisplayName("쉼표가 든 값은 큰따옴표로 감싼다")
    void parseQuoted() {
        String csv = "deliberate,\"의도적인, 신중한\"";

        List<CsvCard> cards = CsvCodec.parse(csv);

        assertThat(cards.getFirst().meaning()).isEqualTo("의도적인, 신중한");
    }

    @Test
    @DisplayName("형식이 잘못된 줄은 줄 번호와 함께 거부한다")
    void rejectMalformedLine() {
        String csv = """
                resilient,회복력 있는
                이_줄은_쉼표가_없다
                """;

        assertThatThrownBy(() -> CsvCodec.parse(csv))
                .isInstanceOf(CsvFormatException.class)
                .hasMessageContaining("2번째 줄");
    }

    @Test
    @DisplayName("내보낸 CSV를 다시 읽으면 같은 내용이 된다")
    void roundTrip() {
        List<CardWithTags> cards = List.of(
                new CardWithTags(new Card(1L, 1L, "deliberate", "의도적인, 신중한", null),
                        List.of("형용사", "TOEIC")),
                new CardWithTags(new Card(2L, 1L, "resilient", "회복력 있는", null),
                        List.of())
        );

        List<CsvCard> reread = CsvCodec.parse(CsvCodec.format(cards));

        assertThat(reread.get(0).meaning()).isEqualTo("의도적인, 신중한");
        assertThat(reread.get(0).tags()).containsExactly("형용사", "TOEIC");
        assertThat(reread.get(1).text()).isEqualTo("resilient");
    }
}

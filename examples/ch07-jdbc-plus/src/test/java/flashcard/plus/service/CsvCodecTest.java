package flashcard.plus.service;

import java.util.List;

import org.junit.jupiter.api.Test;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.CardWithTags;
import flashcard.plus.service.CsvCodec.CsvCard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvCodecTest {

    @Test
    void 원문과_뜻_태그를_읽는다() {
        String csv = """
                resilient,회복력 있는,형용사;TOEIC
                deliberate,의도적인
                """;

        List<CsvCard> cards = CsvCodec.parse(csv);

        assertEquals(2, cards.size());
        assertEquals(List.of("형용사", "TOEIC"), cards.get(0).tags());
        assertTrue(cards.get(1).tags().isEmpty());
    }

    @Test
    void 쉼표가_든_값은_큰따옴표로_감싼다() {
        String csv = "deliberate,\"의도적인, 신중한\"";

        List<CsvCard> cards = CsvCodec.parse(csv);

        assertEquals("의도적인, 신중한", cards.getFirst().meaning());
    }

    @Test
    void 형식이_잘못된_줄은_줄_번호와_함께_거부한다() {
        String csv = """
                resilient,회복력 있는
                이_줄은_쉼표가_없다
                """;

        CsvFormatException e = assertThrows(CsvFormatException.class,
                () -> CsvCodec.parse(csv));

        assertTrue(e.getMessage().contains("2번째 줄"));
    }

    @Test
    void 내보낸_CSV를_다시_읽으면_같은_내용이_된다() {
        List<CardWithTags> cards = List.of(
                new CardWithTags(new Card(1L, 1L, "deliberate", "의도적인, 신중한", null),
                        List.of("형용사", "TOEIC")),
                new CardWithTags(new Card(2L, 1L, "resilient", "회복력 있는", null),
                        List.of())
        );

        List<CsvCard> reread = CsvCodec.parse(CsvCodec.format(cards));

        assertEquals("의도적인, 신중한", reread.get(0).meaning());
        assertEquals(List.of("형용사", "TOEIC"), reread.get(0).tags());
        assertEquals("resilient", reread.get(1).text());
    }
}

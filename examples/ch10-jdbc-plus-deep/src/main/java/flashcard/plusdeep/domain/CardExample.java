package flashcard.plusdeep.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/** 카드에 딸린 예문. 카드 애그리거트에 속한 엔티티다. */
@Table("CARD_EXAMPLE")
public record CardExample(@Id Long id, String sentence, String translation) {

    public static CardExample of(String sentence, String translation) {
        return new CardExample(null, sentence, translation);
    }
}
// end::class[]

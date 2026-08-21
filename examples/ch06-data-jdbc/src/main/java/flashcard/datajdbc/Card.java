package flashcard.datajdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/**
 * 애그리거트에 속한 엔티티.
 * deck_id, card_index 칼럼은 Spring Data JDBC가 대신 관리하므로 필드가 없다.
 * 애너테이션에 적은 이름은 따옴표로 감싼 식별자로 쓰이므로,
 * 따옴표 없는 DDL을 대문자로 저장하는 H2에 맞춰 대문자로 적었다.
 */
@Table("CARDS")
public record Card(@Id Long id, String text, String meaning) {

    public static Card of(String text, String meaning) {
        return new Card(null, text, meaning);
    }
}
// end::class[]

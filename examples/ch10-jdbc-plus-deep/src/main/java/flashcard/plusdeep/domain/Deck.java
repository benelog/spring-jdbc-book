package flashcard.plusdeep.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.navercorp.spring.jdbc.plus.commons.annotations.SoftDeleteColumn;

// tag::class[]
/**
 * soft delete가 적용된 덱.
 * 리포지토리로 삭제하면 DELETE 대신 deleted 컬럼을 true로 바꾸는 UPDATE가 나간다.
 */
@Table("DECK")
public record Deck(@Id Long id,
                   String name,
                   @SoftDeleteColumn(type = SoftDeleteColumn.ValueType.BOOLEAN, valueAsDeleted = "true")
                   boolean deleted) {

    public static Deck create(String name) {
        return new Deck(null, name, false);
    }

    public Deck restored() {
        return new Deck(id, name, false);
    }
}
// end::class[]

package flashcard.plus.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/** 카드를 담아 두는 대신 "이런 카드를 모아 달라"는 조건만 적어 두는 덱. */
@Table("SMART_DECK")
public record SmartDeck(@Id Long id, String name, SmartCondition conditionType, String param) {

    public static SmartDeck create(String name, SmartCondition conditionType, String param) {
        return new SmartDeck(null, name, conditionType, param);
    }
}
// end::class[]

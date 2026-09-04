package flashcard.datajdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 식별자 인용 테스트용 엔티티.
 * Deck과 같은 decks 테이블을 가리키되, 이름을 PostgreSQL이 저장하는 형태인 소문자로 적었다.
 */
// tag::class[]
@Table("decks")
public record LowerDeck(@Id Long id, String name) {
}
// end::class[]

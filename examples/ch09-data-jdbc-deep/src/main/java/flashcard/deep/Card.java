package flashcard.deep;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/**
 * 독립 애그리거트인 카드.
 * 다른 애그리거트(덱)는 객체 참조 대신 AggregateReference로 id만 쥔다.
 */
@Table("CARD")
public record Card(@Id Long id,
                   @Column("DECK_ID") AggregateReference<Deck, Long> deck,
                   String text,
                   String meaning,
                   Tags tags,
                   @CreatedDate LocalDateTime createdAt,
                   @LastModifiedDate LocalDateTime updatedAt) {

    public static Card create(Long deckId, String text, String meaning, Tags tags) {
        return new Card(null, AggregateReference.to(deckId), text, meaning, tags, null, null);
    }

    public Card edit(String newText, String newMeaning) {
        return new Card(id, deck, newText, newMeaning, tags, createdAt, updatedAt);
    }

    /** 앞뒤 공백을 정리한 사본. 저장 직전에 콜백이 호출한다. */
    public Card trimmed() {
        return new Card(id, deck, text.strip(), meaning.strip(), tags, createdAt, updatedAt);
    }
}
// end::class[]

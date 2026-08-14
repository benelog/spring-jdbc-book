package flashcard.plus.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
@Table("CARD")
public record Card(@Id Long id, Long deckId, String text, String meaning,
                   LocalDateTime createdAt) {

    public static Card create(Long deckId, String text, String meaning, LocalDateTime now) {
        return new Card(null, deckId, text, meaning, now);
    }

    public Card edit(String newText, String newMeaning) {
        return new Card(id, deckId, newText, newMeaning, createdAt);
    }
}
// end::class[]

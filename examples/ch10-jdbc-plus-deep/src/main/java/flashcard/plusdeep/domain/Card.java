package flashcard.plusdeep.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/**
 * 예문을 거느린 카드 애그리거트.
 * saveBatch가 getter로 값을 읽으므로 record 대신 JavaBean 스타일 클래스로 만들었다.
 */
@Table("CARD")
public class Card {

    @Id
    private Long id;

    private Long deckId;

    private String text;

    private String meaning;

    @MappedCollection(idColumn = "CARD_ID", keyColumn = "EXAMPLE_INDEX")
    private List<CardExample> examples = new ArrayList<>();

    public Card(Long deckId, String text, String meaning) {
        this.deckId = deckId;
        this.text = text;
        this.meaning = meaning;
    }

    public void addExample(String sentence, String translation) {
        examples.add(CardExample.of(sentence, translation));
    }
    // end::class[]

    public Long getId() {
        return id;
    }

    public Long getDeckId() {
        return deckId;
    }

    public String getText() {
        return text;
    }

    public String getMeaning() {
        return meaning;
    }

    public List<CardExample> getExamples() {
        return List.copyOf(examples);
    }

    @Override
    public String toString() {
        return "Card{id=%d, text='%s', examples=%d}".formatted(id, text, examples.size());
    }
}

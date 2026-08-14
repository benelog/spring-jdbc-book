package flashcard.datajdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/** 애그리거트 루트. 덱을 저장하면 카드도 함께 저장·삭제된다. */
@Table("DECKS")
public class Deck {

    @Id
    private Long id;

    private String name;

    @Version
    private long version;

    @MappedCollection(idColumn = "DECK_ID", keyColumn = "CARD_INDEX")
    private List<Card> cards = new ArrayList<>();

    public Deck(String name) {
        this.name = name;
    }

    public void addCard(String text, String meaning) {
        cards.add(Card.of(text, meaning));
    }

    public void removeCard(Long cardId) {
        cards.removeIf(card -> cardId.equals(card.id()));
    }
    // end::class[]

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public long getVersion() {
        return version;
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    @Override
    public String toString() {
        return "Deck{id=%d, name='%s', cards=%s}".formatted(id, name, cards);
    }
}

package flashcard.aop;

public record Card(Long id, Long deckId, String text, String meaning) {

    public static Card of(Long deckId, String text, String meaning) {
        return new Card(null, deckId, text, meaning);
    }
}

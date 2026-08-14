package flashcard.aop;

/** 덱 가져오기가 끝났음을 알리는 이벤트. */
public record DeckImported(long deckId, String deckName, int cardCount) {
}

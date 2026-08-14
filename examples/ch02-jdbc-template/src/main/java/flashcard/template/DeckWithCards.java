package flashcard.template;

import java.util.List;

/** 덱 하나와 그에 속한 카드들을 함께 담는 조회 전용 record. */
public record DeckWithCards(long id, String name, List<Card> cards) {
}

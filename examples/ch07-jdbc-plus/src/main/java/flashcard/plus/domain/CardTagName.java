package flashcard.plus.domain;

/** (카드 id, 태그 이름) 한 쌍. 덱의 태그 목록을 한 번에 조회할 때 쓴다. */
public record CardTagName(Long cardId, String tagName) {
}

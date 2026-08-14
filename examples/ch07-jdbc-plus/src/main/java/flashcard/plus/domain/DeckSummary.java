package flashcard.plus.domain;

// tag::class[]
/** 홈 화면용 덱 요약: 카드 수와 오늘 복습할 카드 수. */
public record DeckSummary(Long id, String name, long cardCount, long dueCount) {
}
// end::class[]

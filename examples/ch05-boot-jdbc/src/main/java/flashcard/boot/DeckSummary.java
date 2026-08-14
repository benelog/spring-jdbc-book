package flashcard.boot;

// tag::class[]
/** 홈 화면에 보여 줄 덱 요약: 덱 이름과 카드 수. */
public record DeckSummary(Long id, String name, long cardCount) {
}
// end::class[]

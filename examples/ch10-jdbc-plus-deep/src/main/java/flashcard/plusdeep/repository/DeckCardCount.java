package flashcard.plusdeep.repository;

// tag::class[]
/** 통계 화면용 조회 전용 record. */
public record DeckCardCount(String deckName, long cardCount) {
}
// end::class[]

package flashcard.plus.domain;

/** 덱별 성취도. */
public record DeckStat(Long deckId, String deckName, long totalCount, long correctCount) {

    public int accuracyPercent() {
        if (totalCount == 0) {
            return 0;
        }
        return (int) Math.round(correctCount * 100.0 / totalCount);
    }
}

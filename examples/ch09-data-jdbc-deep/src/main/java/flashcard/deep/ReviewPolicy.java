package flashcard.deep;

// tag::class[]
/**
 * 덱마다 다르게 정하는 복습 정책.
 * 테이블이 따로 없는 값 객체로, 덱 테이블의 POLICY_* 컬럼에 펼쳐져 저장된다.
 */
public record ReviewPolicy(int newCardsPerDay, int maxReviewsPerDay) {

    public static ReviewPolicy standard() {
        return new ReviewPolicy(20, 100);
    }
}
// end::class[]

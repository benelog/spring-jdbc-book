package flashcard.plus.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/**
 * 카드 한 장의 복습 일정과 누적 성적.
 * 간격 반복(Spaced Repetition)의 핵심 로직이 이 record 안에 있다.
 */
@Table("REVIEW_STATE")
public record ReviewState(@Id Long id, Long cardId, int intervalDays, LocalDate dueDate,
                          int correctCount, int wrongCount, LocalDateTime lastReviewedAt) {

    static final int FIRST_INTERVAL = 1;
    static final int SECOND_INTERVAL = 6;
    static final double MULTIPLIER = 2.5;

    public static ReviewState initial(Long cardId) {
        return new ReviewState(null, cardId, 0, null, 0, 0, null);
    }

    // tag::srs[]
    /**
     * 맞으면 간격이 1일 -> 6일 -> 2.5배씩으로 늘어나고,
     * 틀리면 1일로 돌아온다.
     */
    public ReviewState reviewed(boolean correct, LocalDateTime now) {
        int nextInterval = correct ? nextIntervalOnCorrect() : FIRST_INTERVAL;
        return new ReviewState(
                id, cardId,
                nextInterval,
                now.toLocalDate().plusDays(nextInterval),
                correctCount + (correct ? 1 : 0),
                wrongCount + (correct ? 0 : 1),
                now
        );
    }

    private int nextIntervalOnCorrect() {
        if (intervalDays == 0) {
            return FIRST_INTERVAL;
        }
        if (intervalDays == FIRST_INTERVAL) {
            return SECOND_INTERVAL;
        }
        return (int) Math.round(intervalDays * MULTIPLIER);
    }
    // end::srs[]

    public int totalCount() {
        return correctCount + wrongCount;
    }
}
// end::class[]

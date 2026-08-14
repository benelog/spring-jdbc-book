package flashcard.plus.domain;

import java.time.LocalDate;

// tag::class[]
/** 하루치 학습량: 정답 수와 오답 수. */
public record DailyStat(LocalDate studyDate, long correctCount, long wrongCount) {

    public long total() {
        return correctCount + wrongCount;
    }
}
// end::class[]

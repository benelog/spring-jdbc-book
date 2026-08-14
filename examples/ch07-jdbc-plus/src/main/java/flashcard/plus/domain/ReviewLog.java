package flashcard.plus.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/** 판정 한 번마다 한 행씩 남는 학습 기록. 통계의 원천 데이터다. */
@Table("REVIEW_LOG")
public record ReviewLog(@Id Long id, Long cardId, boolean correct, boolean retryRound,
                        LocalDateTime reviewedAt, LocalDate studyDate) {

    public static ReviewLog of(Long cardId, boolean correct, boolean retryRound,
                               LocalDateTime now) {
        return new ReviewLog(null, cardId, correct, retryRound, now, now.toLocalDate());
    }
}
// end::class[]

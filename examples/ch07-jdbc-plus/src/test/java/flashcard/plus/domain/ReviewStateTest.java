package flashcard.plus.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// tag::class[]
class ReviewStateTest {

    LocalDateTime now = LocalDateTime.of(2026, 8, 14, 21, 0);

    @Test
    @DisplayName("처음 맞히면 1일 뒤가 다음 복습이다")
    void firstCorrect() {
        ReviewState state = ReviewState.initial(1L).reviewed(true, now);

        assertThat(state.intervalDays()).isEqualTo(1);
        assertThat(state.dueDate()).isEqualTo(now.toLocalDate().plusDays(1));
        assertThat(state.correctCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("간격은 1일, 6일, 그다음은 2배반씩 늘어난다")
    void intervalGrows() {
        ReviewState state = ReviewState.initial(1L)
                .reviewed(true, now)                 // 1일
                .reviewed(true, now.plusDays(1))     // 6일
                .reviewed(true, now.plusDays(7));    // 15일

        assertThat(state.intervalDays()).isEqualTo(15);

        state = state.reviewed(true, now.plusDays(22));
        assertThat(state.intervalDays()).isEqualTo(38);  // 15 * 2.5 = 37.5 -> 38
    }

    @Test
    @DisplayName("틀리면 1일 뒤로 돌아온다")
    void wrongResetsInterval() {
        ReviewState state = ReviewState.initial(1L)
                .reviewed(true, now)
                .reviewed(true, now.plusDays(1))     // 간격 6일
                .reviewed(false, now.plusDays(7));   // 틀림

        assertThat(state.intervalDays()).isEqualTo(1);
        assertThat(state.dueDate()).isEqualTo(now.toLocalDate().plusDays(8));
        assertThat(state.correctCount()).isEqualTo(2);
        assertThat(state.wrongCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("복습 전에는 일정이 없다")
    void noScheduleBeforeReview() {
        ReviewState state = ReviewState.initial(1L);

        assertThat(state.dueDate()).isNull();
        assertThat(state.totalCount()).isZero();
    }
}
// end::class[]

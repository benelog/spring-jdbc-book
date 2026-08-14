package flashcard.plus.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// tag::class[]
class ReviewStateTest {

    LocalDateTime now = LocalDateTime.of(2026, 8, 14, 21, 0);

    @Test
    void 처음_맞히면_1일_뒤가_다음_복습이다() {
        ReviewState state = ReviewState.initial(1L).reviewed(true, now);

        assertEquals(1, state.intervalDays());
        assertEquals(now.toLocalDate().plusDays(1), state.dueDate());
        assertEquals(1, state.correctCount());
    }

    @Test
    void 간격은_1일_6일_그다음은_2배반씩_늘어난다() {
        ReviewState state = ReviewState.initial(1L)
                .reviewed(true, now)                 // 1일
                .reviewed(true, now.plusDays(1))     // 6일
                .reviewed(true, now.plusDays(7));    // 15일

        assertEquals(15, state.intervalDays());

        state = state.reviewed(true, now.plusDays(22));
        assertEquals(38, state.intervalDays());      // 15 * 2.5 = 37.5 -> 38
    }

    @Test
    void 틀리면_1일_뒤로_돌아온다() {
        ReviewState state = ReviewState.initial(1L)
                .reviewed(true, now)
                .reviewed(true, now.plusDays(1))     // 간격 6일
                .reviewed(false, now.plusDays(7));   // 틀림

        assertEquals(1, state.intervalDays());
        assertEquals(now.toLocalDate().plusDays(8), state.dueDate());
        assertEquals(2, state.correctCount());
        assertEquals(1, state.wrongCount());
    }

    @Test
    void 복습_전에는_일정이_없다() {
        ReviewState state = ReviewState.initial(1L);

        assertNull(state.dueDate());
        assertEquals(0, state.totalCount());
    }
}
// end::class[]

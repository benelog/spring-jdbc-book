package flashcard.plus.repository;

import java.time.LocalDate;
import java.util.List;

import flashcard.plus.domain.DailyStat;

public interface ReviewLogRepositoryCustom {

    /** 날짜별 정답·오답 수. 통계 화면의 막대그래프 재료다. */
    List<DailyStat> findDailyStats(LocalDate since);

    /** 학습한 날짜들(최신순). 연속 학습일 계산에 쓴다. */
    List<LocalDate> findStudyDates();

    long countAll();

    long countCorrect();
}

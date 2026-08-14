package flashcard.plus.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.DailyStat;
import flashcard.plus.domain.DeckStat;
import flashcard.plus.repository.DeckRepository;
import flashcard.plus.repository.ReviewLogRepository;

@Service
public class StatsService {

    public static final int CHART_DAYS = 30;

    private final ReviewLogRepository reviewLogRepository;
    private final DeckRepository deckRepository;

    public StatsService(ReviewLogRepository reviewLogRepository, DeckRepository deckRepository) {
        this.reviewLogRepository = reviewLogRepository;
        this.deckRepository = deckRepository;
    }

    // tag::view[]
    public record StatsView(int streakDays, long totalReviews, int accuracyPercent,
                            List<DailyStat> dailyStats, List<DeckStat> deckStats,
                            long maxDailyTotal) {
    }

    @Transactional(readOnly = true)
    public StatsView overview() {
        LocalDate today = LocalDate.now();
        List<DailyStat> daily = padDays(
                reviewLogRepository.findDailyStats(today.minusDays(CHART_DAYS - 1)), today);

        long total = reviewLogRepository.countAll();
        long correct = reviewLogRepository.countCorrect();
        int accuracy = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
        long maxDailyTotal = daily.stream().mapToLong(DailyStat::total).max().orElse(0);

        return new StatsView(streakDays(today), total, accuracy,
                daily, deckRepository.findAllStats(), maxDailyTotal);
    }
    // end::view[]

    // tag::streak[]
    /** 연속 학습일: 오늘(또는 어제)부터 하루도 빠짐없이 이어진 학습 날짜 수. */
    int streakDays(LocalDate today) {
        List<LocalDate> dates = reviewLogRepository.findStudyDates();
        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate anchor = dates.getFirst();
        if (!anchor.equals(today) && !anchor.equals(today.minusDays(1))) {
            return 0;   // 이미 이틀 넘게 쉬었다
        }

        int streak = 1;
        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).equals(anchor.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
    // end::streak[]

    /** 학습이 없던 날도 0으로 채워, 그래프에 빈 날이 그대로 보이게 한다. */
    private List<DailyStat> padDays(List<DailyStat> stats, LocalDate today) {
        Map<LocalDate, DailyStat> byDate = stats.stream()
                .collect(Collectors.toMap(DailyStat::studyDate, Function.identity()));

        List<DailyStat> padded = new ArrayList<>();
        for (int i = CHART_DAYS - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            padded.add(byDate.getOrDefault(date, new DailyStat(date, 0, 0)));
        }
        return padded;
    }
}

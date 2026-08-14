package flashcard.plus.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import flashcard.plus.domain.Card;

// tag::class[]
public interface CardRepositoryCustom {

    List<Card> findByDeckId(Long deckId);

    /** 오늘 복습 큐: 복습 예정일이 지난 카드. */
    List<Card> findDue(LocalDate today);

    long countDue(LocalDate today);

    /** 자주 틀린 카드: 시도 횟수와 오답률 기준. */
    List<Card> findOftenWrong(int minAttempts, int minWrongPercent);

    long countOftenWrong(int minAttempts, int minWrongPercent);

    /** 오래 안 본 카드: 마지막 복습(없으면 생성) 시각이 기준보다 이전. */
    List<Card> findStale(LocalDateTime threshold);

    long countStale(LocalDateTime threshold);

    /** 최근에 추가한 카드. */
    List<Card> findRecent(LocalDateTime threshold);

    /** 특정 태그가 붙은 카드. */
    List<Card> findByTagName(String tagName);
}
// end::class[]

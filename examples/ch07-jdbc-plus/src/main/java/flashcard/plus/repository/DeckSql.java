package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

public class DeckSql extends SqlGeneratorSupport {

    // tag::summary[]
    /** 덱마다 카드 수와 오늘 복습할 카드 수를 함께 센다. */
    public String selectSummaries() {
        return """
                select deck.id,
                       deck.name,
                       count(distinct card.id) as card_count,
                       count(distinct case
                                          when review_state.due_date <= :today then card.id
                           end)                as due_count
                from deck
                    left join card on card.deck_id = deck.id
                    left join review_state on review_state.card_id = card.id
                group by deck.id, deck.name
                order by deck.id
                """;
    }
    // end::summary[]

    // tag::stats[]
    /** 덱별 성취도: 판정 수와 정답 수. */
    public String selectStats() {
        return """
                select deck.id                                            as deck_id,
                       deck.name                                          as deck_name,
                       count(review_log.id)                               as total_count,
                       coalesce(sum(case when review_log.correct then 1 else 0 end), 0)
                                                                          as correct_count
                from deck
                    left join card on card.deck_id = deck.id
                    left join review_log on review_log.card_id = card.id
                group by deck.id, deck.name
                order by deck.id
                """;
    }
    // end::stats[]
}

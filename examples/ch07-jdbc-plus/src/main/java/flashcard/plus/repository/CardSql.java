package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

import flashcard.plus.domain.Card;

// tag::class[]
/**
 * 카드 조회 SQL 모음.
 * sql.columns(Card.class)가 엔티티에 맞는 SELECT 칼럼 목록을 만들어 주므로,
 * 칼럼이 늘어도 SQL을 일일이 고칠 필요가 없다.
 */
public class CardSql extends SqlGeneratorSupport {

    // tag::select-by-deck[]
    public String selectByDeckId() {
        return """
                select %s
                from card
                where deck_id = :deckId
                order by id
                """.formatted(sql.columns(Card.class));
    }
    // end::select-by-deck[]

    // tag::select-due[]
    public String selectDue() {
        return """
                select %s
                from card
                    join review_state on review_state.card_id = card.id
                where review_state.due_date <= :today
                order by review_state.due_date, card.id
                """.formatted(sql.columns(Card.class));
    }

    public String countDue() {
        return """
                select count(*)
                from review_state
                where due_date <= :today
                """;
    }
    // end::select-due[]

    // tag::select-often-wrong[]
    public String selectOftenWrong() {
        return """
                select %s
                from card
                    join review_state on review_state.card_id = card.id
                where review_state.correct_count + review_state.wrong_count >= :minAttempts
                  and review_state.wrong_count * 100 >=
                      (review_state.correct_count + review_state.wrong_count) * :minWrongPercent
                order by review_state.wrong_count desc, card.id
                """.formatted(sql.columns(Card.class));
    }

    public String countOftenWrong() {
        return """
                select count(*)
                from review_state
                where correct_count + wrong_count >= :minAttempts
                  and wrong_count * 100 >= (correct_count + wrong_count) * :minWrongPercent
                """;
    }
    // end::select-often-wrong[]

    public String selectStale() {
        return """
                select %s
                from card
                    left join review_state on review_state.card_id = card.id
                where coalesce(review_state.last_reviewed_at, card.created_at) < :threshold
                order by review_state.last_reviewed_at nulls first, card.id
                """.formatted(sql.columns(Card.class));
    }

    public String countStale() {
        return """
                select count(*)
                from card
                    left join review_state on review_state.card_id = card.id
                where coalesce(review_state.last_reviewed_at, card.created_at) < :threshold
                """;
    }

    public String selectRecent() {
        return """
                select %s
                from card
                where created_at >= :threshold
                order by created_at desc, id desc
                """.formatted(sql.columns(Card.class));
    }

    // tag::select-by-tag[]
    public String selectByTagName() {
        return """
                select %s
                from card
                    join card_tag on card_tag.card_id = card.id
                    join tag on tag.id = card_tag.tag_id
                where tag.name = :tagName
                order by card.id
                """.formatted(sql.columns(Card.class));
    }
    // end::select-by-tag[]
}
// end::class[]

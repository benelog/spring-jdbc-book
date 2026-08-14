package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

import flashcard.plus.domain.ReviewState;

public class ReviewStateSql extends SqlGeneratorSupport {

    public String selectByCardId() {
        return """
                select %s
                from review_state
                where card_id = :cardId
                """.formatted(sql.columns(ReviewState.class));
    }
}

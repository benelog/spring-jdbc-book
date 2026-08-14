package flashcard.plusdeep.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

import flashcard.plusdeep.domain.Deck;

// tag::class[]
public class DeckSql extends SqlGeneratorSupport {

    public String selectByDeleted() {
        return """
                select %s
                from deck
                where deleted = :deleted
                order by id
                """.formatted(sql.columns(Deck.class));
    }
}
// end::class[]

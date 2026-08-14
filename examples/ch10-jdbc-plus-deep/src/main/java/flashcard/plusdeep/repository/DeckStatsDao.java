package flashcard.plusdeep.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcDaoSupport;

// tag::class[]
/**
 * 리포지토리 없이 조회만 담당하는 DAO.
 * 특정 엔티티에 매이지 않으므로 JdbcRepositorySupport 대신 JdbcDaoSupport를 상속한다.
 */
@Repository
public class DeckStatsDao extends JdbcDaoSupport {

    public DeckStatsDao(EntityJdbcProvider entityJdbcProvider) {
        super(entityJdbcProvider);
    }

    public List<DeckCardCount> countCardsPerDeck() {
        return select("""
                        select deck.name as deck_name, count(card.id) as card_count
                        from deck
                            left join card on card.deck_id = deck.id
                        where deck.deleted = false
                        group by deck.id, deck.name
                        order by deck.id
                        """,
                mapParameterSource(),
                (rs, rowNum) -> new DeckCardCount(rs.getString("deck_name"), rs.getLong("card_count")));
    }
}
// end::class[]

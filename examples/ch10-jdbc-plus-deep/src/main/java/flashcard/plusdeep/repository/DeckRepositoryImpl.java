package flashcard.plusdeep.repository;

import java.util.List;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;

import flashcard.plusdeep.domain.Deck;

// tag::class[]
public class DeckRepositoryImpl extends JdbcRepositorySupport<Deck> implements DeckRepositoryCustom {

    private final DeckSql sqls;

    public DeckRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(Deck.class, entityJdbcProvider);
        this.sqls = sqls(DeckSql::new);
    }

    @Override
    public List<Deck> findActive() {
        return find(sqls.selectByDeleted(), mapParameterSource().addValue("deleted", false));
    }

    @Override
    public List<Deck> findTrash() {
        return find(sqls.selectByDeleted(), mapParameterSource().addValue("deleted", true));
    }
}
// end::class[]

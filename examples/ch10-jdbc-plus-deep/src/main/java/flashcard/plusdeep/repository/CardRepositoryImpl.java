package flashcard.plusdeep.repository;

import java.util.List;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;

import flashcard.plusdeep.domain.Card;

// tag::class[]
public class CardRepositoryImpl extends JdbcRepositorySupport<Card> implements CardRepositoryCustom {

    private final CardSql sqls;

    public CardRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(Card.class, entityJdbcProvider);
        this.sqls = sqls(CardSql::new);
    }

    // tag::aggregate[]
    @Override
    public List<Card> searchWithExamples(String keyword) {
        return find(sqls.selectByKeyword(),
                mapParameterSource().addValue("pattern", "%" + keyword + "%"),
                getAggregateResultSetExtractor(Card.class));
    }
    // end::aggregate[]

    // tag::batch[]
    @Override
    public int[] insertBatch(List<Card> cards) {
        return saveBatch(sqls.insertCard(), cards);
    }
    // end::batch[]
}
// end::class[]

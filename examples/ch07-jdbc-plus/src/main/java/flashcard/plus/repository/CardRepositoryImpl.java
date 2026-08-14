package flashcard.plus.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;
import com.navercorp.spring.data.jdbc.plus.sql.support.trait.SingleValueSelectTrait;

import flashcard.plus.domain.Card;

// tag::class[]
public class CardRepositoryImpl extends JdbcRepositorySupport<Card>
        implements CardRepositoryCustom, SingleValueSelectTrait {

    private final CardSql sqls;

    public CardRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(Card.class, entityJdbcProvider);
        this.sqls = sqls(CardSql::new);
    }

    // tag::find-by-deck[]
    @Override
    public List<Card> findByDeckId(Long deckId) {
        return find(sqls.selectByDeckId(),
                mapParameterSource().addValue("deckId", deckId));
    }
    // end::find-by-deck[]

    // tag::find-due[]
    @Override
    public List<Card> findDue(LocalDate today) {
        return find(sqls.selectDue(),
                mapParameterSource().addValue("today", today));
    }

    @Override
    public long countDue(LocalDate today) {
        return selectSingleValue(sqls.countDue(),
                mapParameterSource().addValue("today", today), Long.class);
    }
    // end::find-due[]

    @Override
    public List<Card> findOftenWrong(int minAttempts, int minWrongPercent) {
        return find(sqls.selectOftenWrong(), mapParameterSource()
                .addValue("minAttempts", minAttempts)
                .addValue("minWrongPercent", minWrongPercent));
    }

    @Override
    public long countOftenWrong(int minAttempts, int minWrongPercent) {
        return selectSingleValue(sqls.countOftenWrong(), mapParameterSource()
                .addValue("minAttempts", minAttempts)
                .addValue("minWrongPercent", minWrongPercent), Long.class);
    }

    @Override
    public List<Card> findStale(LocalDateTime threshold) {
        return find(sqls.selectStale(),
                mapParameterSource().addValue("threshold", threshold));
    }

    @Override
    public long countStale(LocalDateTime threshold) {
        return selectSingleValue(sqls.countStale(),
                mapParameterSource().addValue("threshold", threshold), Long.class);
    }

    @Override
    public List<Card> findRecent(LocalDateTime threshold) {
        return find(sqls.selectRecent(),
                mapParameterSource().addValue("threshold", threshold));
    }

    @Override
    public List<Card> findByTagName(String tagName) {
        return find(sqls.selectByTagName(),
                mapParameterSource().addValue("tagName", tagName));
    }
}
// end::class[]

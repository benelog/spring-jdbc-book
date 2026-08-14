package flashcard.plus.repository;

import java.time.LocalDate;
import java.util.List;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;

import org.springframework.jdbc.core.DataClassRowMapper;

import flashcard.plus.domain.Deck;
import flashcard.plus.domain.DeckStat;
import flashcard.plus.domain.DeckSummary;

// tag::class[]
public class DeckRepositoryImpl extends JdbcRepositorySupport<Deck>
        implements DeckRepositoryCustom {

    private final DeckSql sqls;

    public DeckRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(Deck.class, entityJdbcProvider);
        this.sqls = sqls(DeckSql::new);
    }

    // tag::dto[]
    /** 엔티티가 아닌 조회 전용 record는 RowMapper를 지정해 받는다. */
    @Override
    public List<DeckSummary> findAllSummaries(LocalDate today) {
        return find(sqls.selectSummaries(),
                mapParameterSource().addValue("today", today),
                DataClassRowMapper.newInstance(DeckSummary.class));
    }
    // end::dto[]

    @Override
    public List<DeckStat> findAllStats() {
        return find(sqls.selectStats(), mapParameterSource(),
                DataClassRowMapper.newInstance(DeckStat.class));
    }
}
// end::class[]

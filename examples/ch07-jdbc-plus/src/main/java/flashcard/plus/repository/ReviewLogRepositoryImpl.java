package flashcard.plus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;
import com.navercorp.spring.data.jdbc.plus.sql.support.trait.SingleValueSelectTrait;

import org.springframework.jdbc.core.DataClassRowMapper;

import flashcard.plus.domain.DailyStat;
import flashcard.plus.domain.ReviewLog;

public class ReviewLogRepositoryImpl extends JdbcRepositorySupport<ReviewLog>
        implements ReviewLogRepositoryCustom, SingleValueSelectTrait {

    private final ReviewLogSql sqls;

    public ReviewLogRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(ReviewLog.class, entityJdbcProvider);
        this.sqls = sqls(ReviewLogSql::new);
    }

    @Override
    public List<DailyStat> findDailyStats(LocalDate since) {
        return find(sqls.selectDailyStats(),
                mapParameterSource().addValue("since", since),
                DataClassRowMapper.newInstance(DailyStat.class));
    }

    @Override
    public List<LocalDate> findStudyDates() {
        return getJdbcOperations().queryForList(
                sqls.selectStudyDates(), Map.of(), LocalDate.class);
    }

    @Override
    public long countAll() {
        return selectSingleValue(sqls.countAll(), mapParameterSource(), Long.class);
    }

    @Override
    public long countCorrect() {
        return selectSingleValue(sqls.countCorrect(), mapParameterSource(), Long.class);
    }
}

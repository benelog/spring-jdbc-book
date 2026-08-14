package flashcard.plus.repository;

import java.util.Optional;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;

import flashcard.plus.domain.ReviewState;

public class ReviewStateRepositoryImpl extends JdbcRepositorySupport<ReviewState>
        implements ReviewStateRepositoryCustom {

    private final ReviewStateSql sqls;

    public ReviewStateRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(ReviewState.class, entityJdbcProvider);
        this.sqls = sqls(ReviewStateSql::new);
    }

    @Override
    public Optional<ReviewState> findByCardId(Long cardId) {
        return findOne(sqls.selectByCardId(), mapParameterSource().addValue("cardId", cardId));
    }
}

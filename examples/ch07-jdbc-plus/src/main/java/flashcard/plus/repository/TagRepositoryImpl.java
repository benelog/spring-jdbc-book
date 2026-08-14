package flashcard.plus.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.navercorp.spring.data.jdbc.plus.sql.provider.EntityJdbcProvider;
import com.navercorp.spring.data.jdbc.plus.sql.support.JdbcRepositorySupport;

import org.springframework.jdbc.core.DataClassRowMapper;

import flashcard.plus.domain.CardTagName;
import flashcard.plus.domain.Tag;

public class TagRepositoryImpl extends JdbcRepositorySupport<Tag>
        implements TagRepositoryCustom {

    private final TagSql sqls;

    public TagRepositoryImpl(EntityJdbcProvider entityJdbcProvider) {
        super(Tag.class, entityJdbcProvider);
        this.sqls = sqls(TagSql::new);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return findOne(sqls.selectByName(), mapParameterSource().addValue("name", name));
    }

    @Override
    public List<CardTagName> findTagNamesByDeckId(Long deckId) {
        return find(sqls.selectTagNamesByDeckId(),
                mapParameterSource().addValue("deckId", deckId),
                DataClassRowMapper.newInstance(CardTagName.class));
    }

    @Override
    public List<String> findTagNamesByCardId(Long cardId) {
        return getJdbcOperations().queryForList(
                sqls.selectTagNamesByCardId(), Map.of("cardId", cardId), String.class);
    }

    @Override
    public List<String> findAllNames() {
        return getJdbcOperations().queryForList(sqls.selectAllNames(), Map.of(), String.class);
    }

    // tag::update[]
    /** 조회가 아닌 DML은 NamedParameterJdbcTemplate으로 그대로 내려간다. */
    @Override
    public void attach(Long cardId, Long tagId) {
        getJdbcOperations().update(sqls.attach(),
                Map.of("cardId", cardId, "tagId", tagId));
    }
    // end::update[]

    @Override
    public void detachAll(Long cardId) {
        getJdbcOperations().update(sqls.detachAll(), Map.of("cardId", cardId));
    }
}

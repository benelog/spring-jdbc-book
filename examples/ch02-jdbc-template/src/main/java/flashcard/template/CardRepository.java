package flashcard.template;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 이름 있는 파라미터(:name)를 쓰는 NamedParameterJdbcTemplate 기반 저장소.
 * 이 책에서 기본으로 권장하는 방식이다.
 */
public class CardRepository {

    // tag::init[]
    private final NamedParameterJdbcTemplate jdbc;

    private final RowMapper<Card> cardMapper = DataClassRowMapper.newInstance(Card.class);

    public CardRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }
    // end::init[]

    // tag::insert[]
    public long insert(Card card) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        SqlParameterSource params = new BeanPropertySqlParameterSource(card);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
    // end::insert[]

    // tag::select-one[]
    public Optional<Card> findById(long id) {
        String sql = "select id, deck_id, text, meaning from cards where id = :id";
        try {
            Card card = jdbc.queryForObject(sql, Map.of("id", id), cardMapper);
            return Optional.ofNullable(card);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    // end::select-one[]

    // tag::select-list[]
    public List<Card> findByDeckId(long deckId) {
        String sql = """
                select id, deck_id, text, meaning
                from cards
                where deck_id = :deckId
                order by id
                """;
        return jdbc.query(sql, Map.of("deckId", deckId), cardMapper);
    }
    // end::select-list[]

    // tag::in-clause[]
    public List<Card> findByIds(List<Long> ids) {
        String sql = "select id, deck_id, text, meaning from cards where id in (:ids)";
        return jdbc.query(sql, Map.of("ids", ids), cardMapper);
    }
    // end::in-clause[]

    // tag::update[]
    public int update(Card card) {
        String sql = """
                update cards
                set text = :text, meaning = :meaning
                where id = :id
                """;
        return jdbc.update(sql, new BeanPropertySqlParameterSource(card));
    }
    // end::update[]

    public int deleteById(long id) {
        return jdbc.update("delete from cards where id = :id", Map.of("id", id));
    }

    // tag::count[]
    public long countByDeckId(long deckId) {
        String sql = "select count(*) from cards where deck_id = :deckId";
        Long count = jdbc.queryForObject(sql, Map.of("deckId", deckId), Long.class);
        return Objects.requireNonNull(count);
    }
    // end::count[]

    // tag::batch[]
    public int[] insertAll(List<Card> cards) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(cards);
        return jdbc.batchUpdate(sql, batchParams);
    }
    // end::batch[]

    // tag::dynamic-sql[]
    /** 조건이 주어졌을 때만 where 절을 붙이는 동적 검색. */
    public List<Card> search(Long deckId, String keyword) {
        StringBuilder sql = new StringBuilder("""
                select id, deck_id, text, meaning
                from cards
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (deckId != null) {
            sql.append("and deck_id = :deckId\n");
            params.addValue("deckId", deckId);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append("and (text like :keyword or meaning like :keyword)\n");
            params.addValue("keyword", "%" + keyword + "%");
        }
        sql.append("order by id");

        return jdbc.query(sql.toString(), params, cardMapper);
    }
    // end::dynamic-sql[]
}

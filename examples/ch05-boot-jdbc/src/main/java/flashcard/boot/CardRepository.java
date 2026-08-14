package flashcard.boot;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<Card> cardMapper = DataClassRowMapper.newInstance(Card.class);

    public CardRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insert(Card card) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, new BeanPropertySqlParameterSource(card), keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int[] insertAll(List<Card> cards) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        return jdbc.batchUpdate(sql, SqlParameterSourceUtils.createBatch(cards));
    }

    public Optional<Card> findById(long id) {
        String sql = "select id, deck_id, text, meaning from cards where id = :id";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, Map.of("id", id), cardMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Card> findByDeckId(long deckId) {
        String sql = """
                select id, deck_id, text, meaning
                from cards
                where deck_id = :deckId
                order by id
                """;
        return jdbc.query(sql, Map.of("deckId", deckId), cardMapper);
    }

    public int deleteById(long id) {
        return jdbc.update("delete from cards where id = :id", Map.of("id", id));
    }
}

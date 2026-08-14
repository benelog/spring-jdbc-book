package flashcard.tx;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

public class CardRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<Card> cardMapper = DataClassRowMapper.newInstance(Card.class);

    public CardRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    public int[] insertAll(List<Card> cards) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        return jdbc.batchUpdate(sql, SqlParameterSourceUtils.createBatch(cards));
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

    public int moveToDeck(long fromDeckId, long toDeckId) {
        String sql = "update cards set deck_id = :toDeckId where deck_id = :fromDeckId";
        return jdbc.update(sql, Map.of("toDeckId", toDeckId, "fromDeckId", fromDeckId));
    }

    public long countAll() {
        Long count = jdbc.queryForObject("select count(*) from cards", Map.of(), Long.class);
        return Objects.requireNonNull(count);
    }
}

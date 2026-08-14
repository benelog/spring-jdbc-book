package flashcard.aop;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Card> cardMapper = DataClassRowMapper.newInstance(Card.class);

    public CardRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    public int insert(Card card) {
        String sql = """
                insert into cards (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
        return jdbc.update(sql, new BeanPropertySqlParameterSource(card));
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

    public long countAll() {
        Long count = jdbc.queryForObject("select count(*) from cards", Map.of(), Long.class);
        return Objects.requireNonNull(count);
    }
}

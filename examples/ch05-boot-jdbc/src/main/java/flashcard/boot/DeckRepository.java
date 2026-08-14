package flashcard.boot;

import java.util.Map;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// tag::init[]
@Repository
public class DeckRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SimpleJdbcInsert deckInsert;
    private final RowMapper<Deck> deckMapper = DataClassRowMapper.newInstance(Deck.class);
    private final RowMapper<DeckSummary> summaryMapper =
            DataClassRowMapper.newInstance(DeckSummary.class);

    /** Spring Boot가 만들어 둔 NamedParameterJdbcTemplate, JdbcTemplate 빈을 그대로 주입받는다. */
    public DeckRepository(NamedParameterJdbcTemplate jdbc, JdbcTemplate plainJdbc) {
        this.jdbc = jdbc;
        this.deckInsert = new SimpleJdbcInsert(plainJdbc)
                .withTableName("decks")
                .usingGeneratedKeyColumns("id");
    }
    // end::init[]

    public long insert(String name) {
        return deckInsert.executeAndReturnKey(Map.of("name", name)).longValue();
    }

    public Optional<Deck> findById(long id) {
        try {
            Deck deck = jdbc.queryForObject(
                    "select id, name from decks where id = :id", Map.of("id", id), deckMapper);
            return Optional.ofNullable(deck);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // tag::summary[]
    /** 덱마다 카드 수를 함께 세어 온다. */
    public List<DeckSummary> findAllSummaries() {
        String sql = """
                select d.id, d.name, count(c.id) as card_count
                from decks d
                    left join cards c on c.deck_id = d.id
                group by d.id, d.name
                order by d.id
                """;
        return jdbc.query(sql, summaryMapper);
    }
    // end::summary[]

    public int deleteById(long id) {
        return jdbc.update("delete from decks where id = :id", Map.of("id", id));
    }
}

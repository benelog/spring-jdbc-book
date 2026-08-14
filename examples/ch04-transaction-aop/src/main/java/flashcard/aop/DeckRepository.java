package flashcard.aop;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

// tag::init[]
@Repository
public class DeckRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SimpleJdbcInsert deckInsert;
    private final RowMapper<Deck> deckMapper = DataClassRowMapper.newInstance(Deck.class);

    public DeckRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
        this.deckInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("decks")
                .usingGeneratedKeyColumns("id");
    }
    // end::init[]

    public long insert(String name) {
        return deckInsert.executeAndReturnKey(Map.of("name", name)).longValue();
    }

    public List<Deck> findAll() {
        return jdbc.query("select id, name from decks order by id", deckMapper);
    }

    public long countAll() {
        Long count = jdbc.queryForObject("select count(*) from decks", Map.of(), Long.class);
        return Objects.requireNonNull(count);
    }

    public int deleteById(long id) {
        return jdbc.update("delete from decks where id = :id", Map.of("id", id));
    }
}

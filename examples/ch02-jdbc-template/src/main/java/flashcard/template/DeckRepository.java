package flashcard.template;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * 물음표(?) 파라미터를 쓰는 JdbcTemplate 기반 저장소.
 * 파라미터가 적은 단순한 SQL이라면 이 정도로도 충분하다.
 */
public class DeckRepository {

    // tag::init[]
    private final JdbcOperations jdbc;
    private final SimpleJdbcInsert deckInsert;

    public DeckRepository(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        // tag::simple-insert-init[]
        this.deckInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("decks")
                .usingGeneratedKeyColumns("id");
        // end::simple-insert-init[]
    }
    // end::init[]

    // tag::insert[]
    public long insert(String name) {
        Number id = deckInsert.executeAndReturnKey(Map.of("name", name));
        return id.longValue();
    }
    // end::insert[]

    // tag::query[]
    public List<Deck> findAll() {
        return jdbc.query(
                "select id, name from decks order by id",
                (rs, rowNum) -> new Deck(rs.getLong("id"), rs.getString("name"))
        );
    }
    // end::query[]

    // tag::query-for-object[]
    public long countAll() {
        Long count = jdbc.queryForObject("select count(*) from decks", Long.class);
        return Objects.requireNonNull(count);
    }
    // end::query-for-object[]

    // tag::update[]
    public int rename(long id, String newName) {
        return jdbc.update("update decks set name = ? where id = ?", newName, id);
    }
    // end::update[]

    public int deleteById(long id) {
        return jdbc.update("delete from decks where id = ?", id);
    }
}

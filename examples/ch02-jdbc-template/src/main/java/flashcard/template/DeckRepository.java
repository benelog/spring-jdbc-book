package flashcard.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
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

    // tag::duplicate-key[]
    /** 이름이 같은 덱이 이미 있으면 새로 만들지 않고 기존 덱의 id를 돌려준다. */
    public long findOrCreate(String name) {
        try {
            return insert(name);
        } catch (DuplicateKeyException e) {
            Long id = jdbc.queryForObject("select id from decks where name = ?", Long.class, name);
            return Objects.requireNonNull(id);
        }
    }
    // end::duplicate-key[]

    // tag::extractor[]
    /** 덱과 카드를 쿼리 한 번으로 조회해서 계층 구조로 조립한다. */
    public Optional<DeckWithCards> findWithCards(long deckId) {
        String sql = """
                select d.id as deck_id, d.name, c.id as card_id, c.text, c.meaning
                from decks d
                    left join cards c on c.deck_id = d.id
                where d.id = ?
                order by c.id
                """;
        return jdbc.query(sql, rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            long id = rs.getLong("deck_id");
            String name = rs.getString("name");
            List<Card> cards = new ArrayList<>();
            do {
                long cardId = rs.getLong("card_id");
                if (!rs.wasNull()) {   // 카드가 없는 덱은 left join으로 card_id가 null이다
                    cards.add(new Card(cardId, id, rs.getString("text"), rs.getString("meaning")));
                }
            } while (rs.next());
            return Optional.of(new DeckWithCards(id, name, cards));
        }, deckId);
    }
    // end::extractor[]
}

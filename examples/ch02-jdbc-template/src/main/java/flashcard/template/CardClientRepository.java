package flashcard.template;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Spring Framework 6.1에 추가된 JdbcClient 기반 저장소.
 * CardRepository와 같은 일을 fluent API로 처리한다.
 */
public class CardClientRepository {

    // tag::init[]
    private final JdbcClient jdbc;

    private final RowMapper<Card> cardMapper = DataClassRowMapper.newInstance(Card.class);

    public CardClientRepository(DataSource dataSource) {
        this.jdbc = JdbcClient.create(dataSource);
    }
    // end::init[]

    // tag::select-list[]
    public List<Card> findByDeckId(long deckId) {
        return jdbc.sql("""
                        select id, deck_id, text, meaning
                        from cards
                        where deck_id = :deckId
                        order by id
                        """)
                .param("deckId", deckId)
                .query(cardMapper)
                .list();
    }
    // end::select-list[]

    // tag::select-one[]
    public Optional<Card> findById(long id) {
        return jdbc.sql("select id, deck_id, text, meaning from cards where id = :id")
                .param("id", id)
                .query(cardMapper)
                .optional();
    }
    // end::select-one[]

    // tag::count[]
    public long countByDeckId(long deckId) {
        return jdbc.sql("select count(*) from cards where deck_id = :deckId")
                .param("deckId", deckId)
                .query(Long.class)
                .single();
    }
    // end::count[]

    // tag::positional[]
    public List<Card> findByKeyword(String keyword) {
        return jdbc.sql("select id, deck_id, text, meaning from cards where text like ?")
                .param("%" + keyword + "%")
                .query(cardMapper)
                .list();
    }
    // end::positional[]

    // tag::insert[]
    public long insert(Card card) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.sql("""
                        insert into cards (deck_id, text, meaning)
                        values (:deckId, :text, :meaning)
                        """)
                .paramSource(new BeanPropertySqlParameterSource(card))
                .update(keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
    // end::insert[]

    // tag::update[]
    public int update(Card card) {
        return jdbc.sql("""
                        update cards
                        set text = :text, meaning = :meaning
                        where id = :id
                        """)
                .paramSource(new BeanPropertySqlParameterSource(card))
                .update();
    }
    // end::update[]
}

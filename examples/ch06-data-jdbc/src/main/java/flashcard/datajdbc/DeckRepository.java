package flashcard.datajdbc;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

// tag::class[]
public interface DeckRepository extends ListCrudRepository<Deck, Long> {

    // tag::derived[]
    /** 메서드 이름으로 쿼리를 만들어 준다. */
    List<Deck> findByNameContaining(String keyword);
    // end::derived[]

    // tag::query[]
    /** 복잡한 조회는 SQL을 직접 적는다. Spring JDBC에서 하던 그대로다. */
    @Query("""
            select d.id, d.name, count(c.id) as card_count
            from decks d
                left join cards c on c.deck_id = d.id
            group by d.id, d.name
            order by d.id
            """)
    List<DeckSummary> findAllSummaries();

    @Query("""
            select count(*)
            from cards
            where deck_id = :deckId
            """)
    long countCards(@Param("deckId") Long deckId);
    // end::query[]
}
// end::class[]

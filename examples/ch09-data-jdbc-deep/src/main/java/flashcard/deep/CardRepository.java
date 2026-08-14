package flashcard.deep;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

// tag::class[]
public interface CardRepository
        extends ListCrudRepository<Card, Long>, ListPagingAndSortingRepository<Card, Long> {

    // tag::paging[]
    /** 페이징. 반환 타입을 Page로 하면 전체 건수 조회가 함께 실행된다. */
    Page<Card> findByDeck(AggregateReference<Deck, Long> deck, Pageable pageable);
    // end::paging[]

    // tag::limit[]
    /** 상위 N건만. Limit는 페이징 없이 개수만 제한할 때 쓴다. */
    List<Card> findByTextContainingIgnoreCase(String keyword, Limit limit);
    // end::limit[]

    long countByDeck(AggregateReference<Deck, Long> deck);

    // tag::modifying[]
    /** 여러 행을 한 번에 고치는 DML은 @Modifying + @Query로 처리한다. */
    @Modifying
    @Query("update card set deck_id = :toDeckId where deck_id = :fromDeckId")
    int moveAllCards(@Param("fromDeckId") Long fromDeckId, @Param("toDeckId") Long toDeckId);
    // end::modifying[]
}
// end::class[]

package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plus.domain.Card;

// tag::class[]
/**
 * 표준 CRUD는 JdbcRepository가, 조건 검색은 CardRepositoryCustom이 맡는다.
 * insert와 update가 분리되어 있어 SELECT 없이 바로 저장할 수 있다.
 */
public interface CardRepository extends JdbcRepository<Card, Long>, CardRepositoryCustom {
}
// end::class[]

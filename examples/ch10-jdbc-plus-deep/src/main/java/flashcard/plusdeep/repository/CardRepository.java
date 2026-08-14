package flashcard.plusdeep.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plusdeep.domain.Card;

// tag::class[]
public interface CardRepository extends JdbcRepository<Card, Long>, CardRepositoryCustom {
}
// end::class[]

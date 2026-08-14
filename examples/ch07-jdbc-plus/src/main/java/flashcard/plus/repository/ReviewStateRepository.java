package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plus.domain.ReviewState;

public interface ReviewStateRepository
        extends JdbcRepository<ReviewState, Long>, ReviewStateRepositoryCustom {
}

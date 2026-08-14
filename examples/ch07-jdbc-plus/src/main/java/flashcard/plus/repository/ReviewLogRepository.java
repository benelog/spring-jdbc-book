package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plus.domain.ReviewLog;

public interface ReviewLogRepository
        extends JdbcRepository<ReviewLog, Long>, ReviewLogRepositoryCustom {
}

package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import flashcard.plus.domain.Tag;

public interface TagRepository extends JdbcRepository<Tag, Long>, TagRepositoryCustom {
}

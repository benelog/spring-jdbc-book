package flashcard.plus.repository;

import java.util.List;

import com.navercorp.spring.data.jdbc.plus.repository.JdbcRepository;

import org.springframework.data.jdbc.repository.query.Query;

import flashcard.plus.domain.SmartDeck;

// tag::class[]
public interface SmartDeckRepository extends JdbcRepository<SmartDeck, Long> {

    /** 단순한 조회는 Spring Data JDBC의 @Query로도 충분하다. */
    @Query("select id, name, condition_type, param from smart_deck order by id")
    List<SmartDeck> findAllOrdered();
}
// end::class[]

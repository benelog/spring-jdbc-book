package flashcard.boot;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

// tag::testcontainers[]
@Testcontainers(disabledWithoutDocker = true)   // Docker가 없는 환경에서는 건너뛴다
@JdbcTest
@Import({DeckRepository.class, CardRepository.class})
class DeckRepositoryPostgresTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;

    @Test
    @DisplayName("실제 PostgreSQL에서도 덱 요약 쿼리가 동작한다")
    void summariesOnPostgres() {
        long deckId = deckRepository.insert("영어 단어장");
        cardRepository.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인")
        ));

        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertThat(summaries.getFirst().cardCount()).isEqualTo(2);
    }
}
// end::testcontainers[]

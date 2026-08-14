package flashcard.tx;

import java.util.List;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcTransactionManager;

/**
 * TransactionTemplate 실행 예제.
 * 실행: ./gradlew :ch03-transaction-template:run
 */
public class TxQuickTour {

    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        new ResourceDatabasePopulator(new ClassPathResource("schema.sql")).execute(dataSource);

        // tag::wiring[]
        JdbcTransactionManager transactionManager = new JdbcTransactionManager(dataSource);
        DeckRepository deckRepository = new DeckRepository(dataSource);
        CardRepository cardRepository = new CardRepository(dataSource);
        DeckService deckService = new DeckService(transactionManager, deckRepository, cardRepository);
        // end::wiring[]

        long deckId = deckService.importDeck("영어 단어장", List.of(
                Card.of(null, "resilient", "회복력 있는"),
                Card.of(null, "deliberate", "의도적인, 신중한")
        ));
        System.out.println("만들어진 덱 id: " + deckId);
        System.out.println("덱의 카드: " + cardRepository.findByDeckId(deckId));
    }

    static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE");
        config.setUsername("sa");
        return new HikariDataSource(config);
    }
}

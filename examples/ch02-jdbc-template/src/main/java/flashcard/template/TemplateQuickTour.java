package flashcard.template;

import java.util.List;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * JdbcTemplate과 NamedParameterJdbcTemplate을 한 바퀴 도는 실행 예제.
 * 실행: ./gradlew :ch02-jdbc-template:run
 */
public class TemplateQuickTour {

    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        initSchema(dataSource);

        DeckRepository decks = new DeckRepository(dataSource);
        CardRepository cards = new CardRepository(dataSource);

        long deckId = decks.insert("영어 단어장");
        cards.insertAll(List.of(
                Card.of(deckId, "resilient", "회복력 있는"),
                Card.of(deckId, "deliberate", "의도적인, 신중한"),
                Card.of(deckId, "profound", "심오한")
        ));

        System.out.println("전체 덱: " + decks.findAll());
        System.out.println("덱의 카드 수: " + cards.countByDeckId(deckId));
        System.out.println("'회복' 검색 결과: " + cards.search(deckId, "회복"));
    }

    static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE");
        config.setUsername("sa");
        return new HikariDataSource(config);
    }

    // tag::populator[]
    static void initSchema(DataSource dataSource) {
        ResourceDatabasePopulator populator =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        populator.execute(dataSource);
    }
    // end::populator[]
}

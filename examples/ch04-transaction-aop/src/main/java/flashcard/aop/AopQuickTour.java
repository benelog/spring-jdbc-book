package flashcard.aop;

import java.util.List;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * AOP 기반 @Transactional 실행 예제.
 * 실행: ./gradlew :ch04-transaction-aop:run
 */
public class AopQuickTour {

    @Configuration
    @Import(AppConfig.class)
    static class FileDbConfig {

        @Bean
        public DataSource dataSource() {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE");
            config.setUsername("sa");
            return new HikariDataSource(config);
        }
    }

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(FileDbConfig.class)) {
            new ResourceDatabasePopulator(new ClassPathResource("schema.sql"))
                    .execute(context.getBean(DataSource.class));

            DeckService deckService = context.getBean(DeckService.class);
            // tag::proxy[]
            System.out.println("주입된 빈의 실제 클래스: " + deckService.getClass());
            // end::proxy[]

            long deckId = deckService.importDeck("영어 단어장", List.of(
                    Card.of(null, "resilient", "회복력 있는"),
                    Card.of(null, "deliberate", "의도적인, 신중한")
            ));
            System.out.println("덱의 카드: " + deckService.cardsOf(deckId));
        }
    }
}

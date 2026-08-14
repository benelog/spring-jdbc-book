package flashcard.datajdbc;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring Data JDBC 실행 예제.
 * 실행: ./gradlew :ch06-data-jdbc:bootRun
 */
@SpringBootApplication
public class DataJdbcQuickTour {

    // tag::main[]
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(DataJdbcQuickTour.class)
                .web(WebApplicationType.NONE)
                .run(args)) {
            DeckRepository deckRepository = context.getBean(DeckRepository.class);

            Deck deck = new Deck("영어 단어장");
            deck.addCard("resilient", "회복력 있는");
            deck.addCard("deliberate", "의도적인, 신중한");

            Deck saved = deckRepository.save(deck);   // 덱과 카드가 함께 저장된다
            System.out.println("저장된 덱: " + saved);
            System.out.println("카드 수: " + deckRepository.countCards(saved.getId()));
            System.out.println("요약: " + deckRepository.findAllSummaries());
        }
    }
    // end::main[]
}

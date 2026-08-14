package flashcard.deep;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Spring Data JDBC 심화 기능 실행 예제.
 * 실행: ./gradlew :ch09-data-jdbc-deep:bootRun
 */
@SpringBootApplication
public class DataJdbcDeepQuickTour {

    // tag::main[]
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(DataJdbcDeepQuickTour.class)
                .web(WebApplicationType.NONE)
                .run(args)) {
            DeckRepository deckRepository = context.getBean(DeckRepository.class);
            CardRepository cardRepository = context.getBean(CardRepository.class);

            Deck deck = deckRepository.save(new Deck("영어 단어장", ReviewPolicy.standard()));
            for (int i = 1; i <= 12; i++) {
                cardRepository.save(Card.create(deck.getId(), "word" + i, "뜻" + i, Tags.of("영어")));
            }

            var page = cardRepository.findByDeck(
                    org.springframework.data.jdbc.core.mapping.AggregateReference.to(deck.getId()),
                    PageRequest.of(0, 5, Sort.by("id")));
            System.out.println("전체 %d장 중 1페이지: %s".formatted(
                    page.getTotalElements(),
                    page.map(Card::text).getContent()));
            System.out.println("생성 시각(감사): " + deck.getCreatedAt());
        }
    }
    // end::main[]
}

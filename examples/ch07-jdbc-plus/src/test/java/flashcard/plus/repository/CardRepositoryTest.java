package flashcard.plus.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.Deck;
import flashcard.plus.domain.ReviewState;
import flashcard.plus.domain.Tag;

import static org.assertj.core.api.Assertions.assertThat;

// tag::setup[]
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-repo-test;DB_CLOSE_DELAY=-1"
})
@Transactional
class CardRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ReviewStateRepository reviewStateRepository;
    // end::setup[]

    LocalDateTime now = LocalDateTime.now();
    Long deckId;

    @BeforeEach
    void setUp() {
        deckId = deckRepository.insert(Deck.create("영어 단어장", now)).id();
    }

    Card newCard(String text, String meaning, LocalDateTime createdAt) {
        return cardRepository.insert(Card.create(deckId, text, meaning, createdAt));
    }

    @Test
    @DisplayName("덱의 카드를 조회한다")
    void findByDeckId() {
        newCard("resilient", "회복력 있는", now);
        newCard("deliberate", "의도적인", now);

        assertThat(cardRepository.findByDeckId(deckId)).hasSize(2);
    }

    // tag::due[]
    @Test
    @DisplayName("복습 예정일이 지난 카드만 오늘 복습 큐에 들어간다")
    void findDue() {
        Card dueCard = newCard("resilient", "회복력 있는", now);
        Card notDueCard = newCard("deliberate", "의도적인", now);

        LocalDate today = LocalDate.now();
        reviewStateRepository.insert(withDueDate(dueCard, today.minusDays(1)));
        reviewStateRepository.insert(withDueDate(notDueCard, today.plusDays(3)));

        List<Card> due = cardRepository.findDue(today);

        assertThat(due).hasSize(1);
        assertThat(due.getFirst().text()).isEqualTo("resilient");
        assertThat(cardRepository.countDue(today)).isEqualTo(1);
    }
    // end::due[]

    @Test
    @DisplayName("오답률이 높은 카드를 골라낸다")
    void findOftenWrong() {
        Card oftenWrong = newCard("resilient", "회복력 있는", now);
        Card wellKnown = newCard("deliberate", "의도적인", now);

        reviewStateRepository.insert(withCounts(oftenWrong, 2, 2));  // 오답률 50%
        reviewStateRepository.insert(withCounts(wellKnown, 5, 1));   // 오답률 17%

        List<Card> found = cardRepository.findOftenWrong(3, 40);

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().text()).isEqualTo("resilient");
        assertThat(cardRepository.countOftenWrong(3, 40)).isEqualTo(1);
    }

    @Test
    @DisplayName("오래 안 본 카드를 골라낸다")
    void findStale() {
        Card stale = newCard("resilient", "회복력 있는", now.minusDays(30));
        reviewStateRepository.insert(withLastReviewedAt(stale, now.minusDays(10)));

        Card fresh = newCard("deliberate", "의도적인", now.minusDays(30));
        reviewStateRepository.insert(withLastReviewedAt(fresh, now.minusDays(1)));

        newCard("profound", "심오한", now.minusDays(30));  // 복습 기록 없음 -> 생성일 기준

        List<Card> found = cardRepository.findStale(now.minusDays(7));

        assertThat(found).hasSize(2);
        assertThat(cardRepository.countStale(now.minusDays(7))).isEqualTo(2);
    }

    @Test
    @DisplayName("최근에 추가한 카드를 골라낸다")
    void findRecent() {
        newCard("resilient", "회복력 있는", now.minusDays(30));
        newCard("deliberate", "의도적인", now.minusDays(1));

        assertThat(cardRepository.findRecent(now.minusDays(7))).hasSize(1);
    }

    @Test
    @DisplayName("태그가 붙은 카드를 골라낸다")
    void findByTagName() {
        Card tagged = newCard("resilient", "회복력 있는", now);
        newCard("deliberate", "의도적인", now);

        Tag tag = tagRepository.insert(Tag.create("형용사"));
        tagRepository.attach(tagged.id(), tag.id());

        List<Card> found = cardRepository.findByTagName("형용사");

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().text()).isEqualTo("resilient");
    }

    ReviewState withDueDate(Card card, LocalDate dueDate) {
        return new ReviewState(null, card.id(), 1, dueDate, 0, 0, now);
    }

    ReviewState withCounts(Card card, int correct, int wrong) {
        return new ReviewState(null, card.id(), 1, null, correct, wrong, now);
    }

    ReviewState withLastReviewedAt(Card card, LocalDateTime lastReviewedAt) {
        return new ReviewState(null, card.id(), 1, null, 1, 0, lastReviewedAt);
    }
}

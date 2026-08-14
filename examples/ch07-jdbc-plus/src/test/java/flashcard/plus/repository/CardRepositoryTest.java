package flashcard.plus.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.Deck;
import flashcard.plus.domain.ReviewState;
import flashcard.plus.domain.Tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void 덱의_카드를_조회한다() {
        newCard("resilient", "회복력 있는", now);
        newCard("deliberate", "의도적인", now);

        assertEquals(2, cardRepository.findByDeckId(deckId).size());
    }

    // tag::due[]
    @Test
    void 복습_예정일이_지난_카드만_오늘_복습_큐에_들어간다() {
        Card dueCard = newCard("resilient", "회복력 있는", now);
        Card notDueCard = newCard("deliberate", "의도적인", now);

        LocalDate today = LocalDate.now();
        reviewStateRepository.insert(withDueDate(dueCard, today.minusDays(1)));
        reviewStateRepository.insert(withDueDate(notDueCard, today.plusDays(3)));

        List<Card> due = cardRepository.findDue(today);

        assertEquals(1, due.size());
        assertEquals("resilient", due.getFirst().text());
        assertEquals(1, cardRepository.countDue(today));
    }
    // end::due[]

    @Test
    void 오답률이_높은_카드를_골라낸다() {
        Card oftenWrong = newCard("resilient", "회복력 있는", now);
        Card wellKnown = newCard("deliberate", "의도적인", now);

        reviewStateRepository.insert(withCounts(oftenWrong, 2, 2));  // 오답률 50%
        reviewStateRepository.insert(withCounts(wellKnown, 5, 1));   // 오답률 17%

        List<Card> found = cardRepository.findOftenWrong(3, 40);

        assertEquals(1, found.size());
        assertEquals("resilient", found.getFirst().text());
        assertEquals(1, cardRepository.countOftenWrong(3, 40));
    }

    @Test
    void 오래_안_본_카드를_골라낸다() {
        Card stale = newCard("resilient", "회복력 있는", now.minusDays(30));
        reviewStateRepository.insert(withLastReviewedAt(stale, now.minusDays(10)));

        Card fresh = newCard("deliberate", "의도적인", now.minusDays(30));
        reviewStateRepository.insert(withLastReviewedAt(fresh, now.minusDays(1)));

        newCard("profound", "심오한", now.minusDays(30));  // 복습 기록 없음 -> 생성일 기준

        List<Card> found = cardRepository.findStale(now.minusDays(7));

        assertEquals(2, found.size());
        assertEquals(2, cardRepository.countStale(now.minusDays(7)));
    }

    @Test
    void 최근에_추가한_카드를_골라낸다() {
        newCard("resilient", "회복력 있는", now.minusDays(30));
        newCard("deliberate", "의도적인", now.minusDays(1));

        assertEquals(1, cardRepository.findRecent(now.minusDays(7)).size());
    }

    @Test
    void 태그가_붙은_카드를_골라낸다() {
        Card tagged = newCard("resilient", "회복력 있는", now);
        newCard("deliberate", "의도적인", now);

        Tag tag = tagRepository.insert(Tag.create("형용사"));
        tagRepository.attach(tagged.id(), tag.id());

        List<Card> found = cardRepository.findByTagName("형용사");

        assertEquals(1, found.size());
        assertEquals("resilient", found.getFirst().text());
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

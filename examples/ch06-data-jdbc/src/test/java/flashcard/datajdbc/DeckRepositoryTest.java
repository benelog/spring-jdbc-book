package flashcard.datajdbc;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// tag::setup[]
@DataJdbcTest
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    // end::setup[]

    // tag::aggregate-save[]
    @Test
    void 덱을_저장하면_카드도_함께_저장된다() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deck.addCard("deliberate", "의도적인");

        Deck saved = deckRepository.save(deck);

        Deck found = deckRepository.findById(saved.getId()).orElseThrow();
        assertEquals(2, found.getCards().size());
        assertEquals("resilient", found.getCards().getFirst().text());
    }
    // end::aggregate-save[]

    // tag::aggregate-remove[]
    @Test
    void 카드를_빼고_저장하면_카드_행도_지워진다() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deck.addCard("deliberate", "의도적인");
        Deck saved = deckRepository.save(deck);

        Long removedCardId = saved.getCards().getFirst().id();
        saved.removeCard(removedCardId);
        deckRepository.save(saved);

        Deck found = deckRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1, found.getCards().size());
        assertEquals(1, deckRepository.countCards(saved.getId()));
    }
    // end::aggregate-remove[]

    @Test
    void 덱을_지우면_카드도_함께_지워진다() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        Deck saved = deckRepository.save(deck);

        deckRepository.deleteById(saved.getId());

        assertTrue(deckRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void 이름으로_검색한다() {
        deckRepository.save(new Deck("영어 단어장"));
        deckRepository.save(new Deck("전공 용어"));

        List<Deck> found = deckRepository.findByNameContaining("단어");

        assertEquals(1, found.size());
    }

    @Test
    void 덱_요약을_SQL로_조회한다() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deckRepository.save(deck);

        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertEquals(1, summaries.size());
        assertEquals(1, summaries.getFirst().cardCount());
    }

    // tag::optimistic-lock[]
    @Test
    void 낡은_버전으로_저장하면_낙관적_잠금_예외가_난다() {
        Deck saved = deckRepository.save(new Deck("영어 단어장"));

        Deck copy1 = deckRepository.findById(saved.getId()).orElseThrow();
        Deck copy2 = deckRepository.findById(saved.getId()).orElseThrow();

        copy1.rename("TOEIC 단어장");
        deckRepository.save(copy1);           // version이 올라간다

        copy2.rename("수능 단어장");
        assertThrows(OptimisticLockingFailureException.class,
                () -> deckRepository.save(copy2));  // 낡은 version → 실패
    }
    // end::optimistic-lock[]
}

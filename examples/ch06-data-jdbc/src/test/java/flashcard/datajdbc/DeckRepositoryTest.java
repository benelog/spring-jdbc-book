package flashcard.datajdbc;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// tag::setup[]
@DataJdbcTest
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    // end::setup[]

    // tag::aggregate-save[]
    @Test
    @DisplayName("덱을 저장하면 카드도 함께 저장된다")
    void saveAggregate() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deck.addCard("deliberate", "의도적인");

        Deck saved = deckRepository.save(deck);

        Deck found = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getCards()).hasSize(2);
        assertThat(found.getCards().getFirst().text()).isEqualTo("resilient");
    }
    // end::aggregate-save[]

    // tag::aggregate-remove[]
    @Test
    @DisplayName("카드를 빼고 저장하면 카드 행도 지워진다")
    void removeCardOnSave() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deck.addCard("deliberate", "의도적인");
        Deck saved = deckRepository.save(deck);

        Long removedCardId = saved.getCards().getFirst().id();
        saved.removeCard(removedCardId);
        deckRepository.save(saved);

        Deck found = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getCards()).hasSize(1);
        assertThat(deckRepository.countCards(saved.getId())).isEqualTo(1);
    }
    // end::aggregate-remove[]

    @Test
    @DisplayName("덱을 지우면 카드도 함께 지워진다")
    void deleteCascades() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        Deck saved = deckRepository.save(deck);

        deckRepository.deleteById(saved.getId());

        assertThat(deckRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("이름으로 검색한다")
    void findByName() {
        deckRepository.save(new Deck("영어 단어장"));
        deckRepository.save(new Deck("전공 용어"));

        List<Deck> found = deckRepository.findByNameContaining("단어");

        assertThat(found).hasSize(1);
    }

    @Test
    @DisplayName("덱 요약을 SQL로 조회한다")
    void findSummaries() {
        Deck deck = new Deck("영어 단어장");
        deck.addCard("resilient", "회복력 있는");
        deckRepository.save(deck);

        List<DeckSummary> summaries = deckRepository.findAllSummaries();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.getFirst().cardCount()).isEqualTo(1);
    }

    // tag::optimistic-lock[]
    @Test
    @DisplayName("낡은 버전으로 저장하면 낙관적 잠금 예외가 난다")
    void optimisticLock() {
        Deck saved = deckRepository.save(new Deck("영어 단어장"));

        Deck copy1 = deckRepository.findById(saved.getId()).orElseThrow();
        Deck copy2 = deckRepository.findById(saved.getId()).orElseThrow();

        copy1.rename("TOEIC 단어장");
        deckRepository.save(copy1);           // version이 올라간다

        copy2.rename("수능 단어장");
        assertThatThrownBy(() -> deckRepository.save(copy2))  // 낡은 version → 실패
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
    // end::optimistic-lock[]
}

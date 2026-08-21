package flashcard.plusdeep.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plusdeep.domain.Deck;

import static org.assertj.core.api.Assertions.assertThat;

// tag::setup[]
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-deep-soft-delete;DB_CLOSE_DELAY=-1"
})
@Transactional
class SoftDeleteTest {

    @Autowired
    DeckRepository deckRepository;
    // end::setup[]

    // tag::soft-delete[]
    @Test
    @DisplayName("삭제하면 행이 지워지는 대신 deleted가 true로 바뀐다")
    void deleteBecomesUpdate() {
        Deck saved = deckRepository.insert(Deck.create("영어 단어장"));

        deckRepository.deleteById(saved.id());

        // 행은 그대로 남아 있고, soft delete 칼럼만 바뀌었다
        Deck found = deckRepository.findById(saved.id()).orElseThrow();
        assertThat(found.deleted()).isTrue();
    }
    // end::soft-delete[]

    // tag::trash[]
    @Test
    @DisplayName("휴지통 조회와 복원")
    void trashAndRestore() {
        Deck keep = deckRepository.insert(Deck.create("영어 단어장"));
        Deck trash = deckRepository.insert(Deck.create("지울 단어장"));
        deckRepository.delete(trash);

        assertThat(deckRepository.findActive()).extracting(Deck::name)
                .containsExactly("영어 단어장");
        assertThat(deckRepository.findTrash()).extracting(Deck::name)
                .containsExactly("지울 단어장");

        // 복원은 deleted를 false로 되돌리는 평범한 update다
        Deck restored = deckRepository.findTrash().getFirst().restored();
        deckRepository.update(restored);

        assertThat(deckRepository.findActive()).hasSize(2);
        assertThat(deckRepository.findTrash()).isEmpty();
        assertThat(keep.deleted()).isFalse();
    }
    // end::trash[]
}

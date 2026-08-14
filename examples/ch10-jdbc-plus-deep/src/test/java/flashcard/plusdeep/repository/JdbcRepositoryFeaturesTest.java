package flashcard.plusdeep.repository;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plusdeep.domain.Deck;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-deep-features;DB_CLOSE_DELAY=-1"
})
@Transactional
class JdbcRepositoryFeaturesTest {

    @Autowired
    DeckRepository deckRepository;

    @BeforeEach
    void setUp() {
        deckRepository.insert(Deck.create("영어 단어장"));
        deckRepository.insert(Deck.create("전공 용어"));
        Deck trash = deckRepository.insert(Deck.create("지운 단어장"));
        deckRepository.delete(trash);
    }

    // tag::paging[]
    @Test
    @DisplayName("JdbcRepository는 페이징도 상속받는다")
    void paging() {
        Page<Deck> page = deckRepository.findAll(PageRequest.of(0, 2, Sort.by("name")));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
    }
    // end::paging[]

    // tag::example[]
    @Test
    @DisplayName("예제 객체(Example)로 조회한다")
    void queryByExample() {
        Deck probe = new Deck(null, "영어 단어장", false);

        Iterable<Deck> found = deckRepository.findAll(Example.of(probe));

        assertThat(found).hasSize(1);
        assertThat(found.iterator().next().name()).isEqualTo("영어 단어장");
    }
    // end::example[]

    // tag::criteria[]
    @Test
    @DisplayName("Criteria로 조건을 조립해 스트림으로 받는다")
    void streamAllWithCriteria() {
        try (Stream<Deck> active = deckRepository.streamAll(
                Query.query(Criteria.where("deleted").isFalse()))) {

            List<String> names = active.map(Deck::name).sorted().toList();
            assertThat(names).containsExactly("영어 단어장", "전공 용어");
        }
    }
    // end::criteria[]
}

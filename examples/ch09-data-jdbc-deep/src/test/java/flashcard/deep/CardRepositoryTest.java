package flashcard.deep;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataJdbcDeepConfig.class)
class CardRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    Long deckId;

    @BeforeEach
    void setUp() {
        deckId = deckRepository.save(new Deck("영어 단어장", ReviewPolicy.standard())).getId();
    }

    // tag::aggregate-reference[]
    @Test
    @DisplayName("다른 애그리거트는 id 참조로 연결된다")
    void aggregateReference() {
        Card saved = cardRepository.save(
                Card.create(deckId, "resilient", "회복력 있는", Tags.of("형용사")));

        Card found = cardRepository.findById(saved.id()).orElseThrow();
        assertThat(found.deck().getId()).isEqualTo(deckId);

        Deck deck = deckRepository.findById(found.deck().getId()).orElseThrow();
        assertThat(deck.getName()).isEqualTo("영어 단어장");
    }
    // end::aggregate-reference[]

    // tag::paging[]
    @Test
    @DisplayName("페이지 단위로 끊어 조회한다")
    void paging() {
        for (int i = 1; i <= 12; i++) {
            cardRepository.save(Card.create(deckId, "word%02d".formatted(i), "뜻" + i, Tags.none()));
        }

        Page<Card> page = cardRepository.findByDeck(
                AggregateReference.to(deckId), PageRequest.of(0, 5, Sort.by("id")));

        assertThat(page.getTotalElements()).isEqualTo(12);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getContent().getFirst().text()).isEqualTo("word01");
    }
    // end::paging[]

    // tag::sort-limit[]
    @Test
    @DisplayName("정렬 조건은 호출하는 쪽이 정한다")
    void sorting() {
        cardRepository.save(Card.create(deckId, "banana", "바나나", Tags.none()));
        cardRepository.save(Card.create(deckId, "apple", "사과", Tags.none()));

        List<Card> sorted = cardRepository.findAll(Sort.by(Sort.Direction.DESC, "text"));

        assertThat(sorted).extracting(Card::text).containsExactly("banana", "apple");
    }

    @Test
    @DisplayName("Limit로 상위 N건만 가져온다")
    void limit() {
        cardRepository.save(Card.create(deckId, "wordplay", "말장난", Tags.none()));
        cardRepository.save(Card.create(deckId, "password", "비밀번호", Tags.none()));
        cardRepository.save(Card.create(deckId, "wordsmith", "글솜씨 좋은 사람", Tags.none()));

        List<Card> found = cardRepository.findByTextContainingIgnoreCase("word", Limit.of(2));

        assertThat(found).hasSize(2);
    }
    // end::sort-limit[]

    // tag::converter[]
    @Test
    @DisplayName("태그 묶음이 쉼표로 이은 문자열 한 칼럼으로 저장된다")
    void tagsConverter() {
        Card saved = cardRepository.save(
                Card.create(deckId, "resilient", "회복력 있는", Tags.of("형용사", "TOEIC")));

        String stored = jdbcTemplate.queryForObject(
                "select tags from card where id = :id",
                java.util.Map.of("id", saved.id()), String.class);
        assertThat(stored).isEqualTo("형용사,TOEIC");

        Card found = cardRepository.findById(saved.id()).orElseThrow();
        assertThat(found.tags()).isEqualTo(Tags.of("형용사", "TOEIC"));
    }
    // end::converter[]

    // tag::callback[]
    @Test
    @DisplayName("저장 직전 콜백이 앞뒤 공백을 정리한다")
    void beforeConvertCallback() {
        Card saved = cardRepository.save(
                Card.create(deckId, "  resilient  ", " 회복력 있는 ", Tags.none()));

        assertThat(saved.text()).isEqualTo("resilient");
        assertThat(cardRepository.findById(saved.id()).orElseThrow().meaning())
                .isEqualTo("회복력 있는");
    }
    // end::callback[]

    // tag::modifying[]
    @Test
    @DisplayName("덱의 카드 전부를 다른 덱으로 옮긴다")
    void moveAllCards() {
        Long targetDeckId = deckRepository.save(new Deck("통합 단어장", ReviewPolicy.standard())).getId();
        cardRepository.save(Card.create(deckId, "resilient", "회복력 있는", Tags.none()));
        cardRepository.save(Card.create(deckId, "deliberate", "의도적인", Tags.none()));

        int moved = cardRepository.moveAllCards(deckId, targetDeckId);

        assertThat(moved).isEqualTo(2);
        assertThat(cardRepository.countByDeck(AggregateReference.to(deckId))).isZero();
        assertThat(cardRepository.countByDeck(AggregateReference.to(targetDeckId))).isEqualTo(2);
    }
    // end::modifying[]
}

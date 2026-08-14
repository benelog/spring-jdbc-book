package flashcard.plus.service;

import java.time.LocalDate;
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
import flashcard.plus.repository.ReviewLogRepository;
import flashcard.plus.repository.ReviewStateRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-study-test;DB_CLOSE_DELAY=-1"
})
@Transactional
class StudyServiceTest {

    @Autowired
    DeckService deckService;
    @Autowired
    CardService cardService;
    @Autowired
    StudyService studyService;
    @Autowired
    ReviewStateRepository reviewStateRepository;
    @Autowired
    ReviewLogRepository reviewLogRepository;

    Deck deck;
    Card card1;
    Card card2;

    @BeforeEach
    void setUp() {
        deck = deckService.createDeck("영어 단어장");
        card1 = cardService.addCard(deck.id(), "resilient", "회복력 있는", "");
        card2 = cardService.addCard(deck.id(), "deliberate", "의도적인", "");
    }

    // tag::first-round[]
    @Test
    @DisplayName("첫 라운드의 판정은 복습 일정에 반영된다")
    void firstRound() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);

        studyService.answer(session, true);    // card1 맞음
        studyService.answer(session, false);   // card2 틀림

        ReviewState state1 = reviewStateRepository.findByCardId(card1.id()).orElseThrow();
        assertThat(state1.correctCount()).isEqualTo(1);
        assertThat(state1.dueDate()).isEqualTo(LocalDate.now().plusDays(1));

        ReviewState state2 = reviewStateRepository.findByCardId(card2.id()).orElseThrow();
        assertThat(state2.wrongCount()).isEqualTo(1);

        assertThat(reviewLogRepository.countAll()).isEqualTo(2);
        assertThat(session.isRoundFinished()).isTrue();
        assertThat(session.hasWrongCards()).isTrue();
    }
    // end::first-round[]

    // tag::retry-round[]
    @Test
    @DisplayName("재도전 라운드의 판정은 복습 일정에 반영되지 않는다")
    void retryRound() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);
        studyService.answer(session, true);
        studyService.answer(session, false);

        session.startRetryRound();             // 틀린 card2만 남는다
        assertThat(session.getTotal()).isEqualTo(1);
        assertThat(session.currentCardId()).isEqualTo(card2.id());

        studyService.answer(session, true);    // 재도전에서 맞혔지만

        ReviewState state2 = reviewStateRepository.findByCardId(card2.id()).orElseThrow();
        assertThat(state2.correctCount()).isZero();  // 일정과 성적은 그대로다
        assertThat(state2.wrongCount()).isEqualTo(1);

        assertThat(reviewLogRepository.countAll()).isEqualTo(3);  // 기록은 남는다
        assertThat(session.hasWrongCards()).isFalse();
    }
    // end::retry-round[]

    @Test
    @DisplayName("모두 맞힌 뒤의 오늘 복습 큐는 비어 있다")
    void emptyQueueAfterAllCorrect() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);
        studyService.answer(session, true);
        studyService.answer(session, true);

        assertThat(studyService.todayCount()).isZero();  // 내일이 due
    }

    @Test
    @DisplayName("CSV를 들여오면 한 트랜잭션으로 카드가 생긴다")
    void importCsv() {
        String csv = """
                profound,심오한,형용사
                recover,회복하다
                """;

        int imported = deckService.importCsv(deck.id(), csv);

        assertThat(imported).isEqualTo(2);
        assertThat(cardService.cardsWithTags(deck.id())).hasSize(4);
        assertThat(cardService.cardsWithTags(deck.id()).get(2).tags())
                .containsExactly("형용사");
    }

    @Test
    @DisplayName("CSV로 내보낸 덱을 다시 들여올 수 있다")
    void exportAndReimport() {
        cardService.editCard(card2.id(), "deliberate", "의도적인, 신중한", "형용사");

        String csv = deckService.exportCsv(deck.id());
        Deck copy = deckService.createDeck("복사본");
        deckService.importCsv(copy.id(), csv);

        assertThat(cardService.cardsWithTags(copy.id())).hasSize(2);
        assertThat(cardService.cardsWithTags(copy.id()).get(1).card().meaning())
                .isEqualTo("의도적인, 신중한");
    }
}

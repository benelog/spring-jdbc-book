package flashcard.plus.service;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.Deck;
import flashcard.plus.domain.ReviewState;
import flashcard.plus.repository.ReviewLogRepository;
import flashcard.plus.repository.ReviewStateRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void 첫_라운드의_판정은_복습_일정에_반영된다() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);

        studyService.answer(session, true);    // card1 맞음
        studyService.answer(session, false);   // card2 틀림

        ReviewState state1 = reviewStateRepository.findByCardId(card1.id()).orElseThrow();
        assertEquals(1, state1.correctCount());
        assertEquals(LocalDate.now().plusDays(1), state1.dueDate());

        ReviewState state2 = reviewStateRepository.findByCardId(card2.id()).orElseThrow();
        assertEquals(1, state2.wrongCount());

        assertEquals(2, reviewLogRepository.countAll());
        assertTrue(session.isRoundFinished());
        assertTrue(session.hasWrongCards());
    }
    // end::first-round[]

    // tag::retry-round[]
    @Test
    void 재도전_라운드의_판정은_복습_일정에_반영되지_않는다() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);
        studyService.answer(session, true);
        studyService.answer(session, false);

        session.startRetryRound();             // 틀린 card2만 남는다
        assertEquals(1, session.getTotal());
        assertEquals(card2.id(), session.currentCardId());

        studyService.answer(session, true);    // 재도전에서 맞혔지만

        ReviewState state2 = reviewStateRepository.findByCardId(card2.id()).orElseThrow();
        assertEquals(0, state2.correctCount());  // 일정과 성적은 그대로다
        assertEquals(1, state2.wrongCount());

        assertEquals(3, reviewLogRepository.countAll());  // 기록은 남는다
        assertFalse(session.hasWrongCards());
    }
    // end::retry-round[]

    @Test
    void 모두_맞힌_뒤의_오늘_복습_큐는_비어_있다() {
        StudySession session = studyService.startDeckSession(
                deck.id(), StudyDirection.TEXT_TO_MEANING);
        studyService.answer(session, true);
        studyService.answer(session, true);

        assertEquals(0, studyService.todayCount());  // 내일이 due
    }

    @Test
    void CSV를_들여오면_한_트랜잭션으로_카드가_생긴다() {
        String csv = """
                profound,심오한,형용사
                recover,회복하다
                """;

        int imported = deckService.importCsv(deck.id(), csv);

        assertEquals(2, imported);
        assertEquals(4, cardService.cardsWithTags(deck.id()).size());
        assertEquals(java.util.List.of("형용사"),
                cardService.cardsWithTags(deck.id()).get(2).tags());
    }

    @Test
    void CSV로_내보낸_덱을_다시_들여올_수_있다() {
        cardService.editCard(card2.id(), "deliberate", "의도적인, 신중한", "형용사");

        String csv = deckService.exportCsv(deck.id());
        Deck copy = deckService.createDeck("복사본");
        deckService.importCsv(copy.id(), csv);

        assertEquals(2, cardService.cardsWithTags(copy.id()).size());
        assertEquals("의도적인, 신중한",
                cardService.cardsWithTags(copy.id()).get(1).card().meaning());
    }
}

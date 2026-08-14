package flashcard.plus.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.ReviewLog;
import flashcard.plus.domain.ReviewState;
import flashcard.plus.domain.SmartCondition;
import flashcard.plus.domain.SmartDeck;
import flashcard.plus.repository.CardRepository;
import flashcard.plus.repository.ReviewLogRepository;
import flashcard.plus.repository.ReviewStateRepository;

@Service
public class StudyService {

    private final CardRepository cardRepository;
    private final ReviewStateRepository reviewStateRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final DeckService deckService;
    private final SmartDeckService smartDeckService;

    public StudyService(CardRepository cardRepository,
                        ReviewStateRepository reviewStateRepository,
                        ReviewLogRepository reviewLogRepository,
                        DeckService deckService, SmartDeckService smartDeckService) {
        this.cardRepository = cardRepository;
        this.reviewStateRepository = reviewStateRepository;
        this.reviewLogRepository = reviewLogRepository;
        this.deckService = deckService;
        this.smartDeckService = smartDeckService;
    }

    // tag::start[]
    @Transactional(readOnly = true)
    public StudySession startDeckSession(Long deckId, StudyDirection direction) {
        List<Card> cards = cardRepository.findByDeckId(deckId);
        return new StudySession(deckService.getDeck(deckId).name(), direction, idsOf(cards));
    }

    @Transactional(readOnly = true)
    public StudySession startTodaySession(StudyDirection direction) {
        List<Card> cards = cardRepository.findDue(LocalDate.now());
        return new StudySession("오늘 복습", direction, idsOf(cards));
    }

    @Transactional(readOnly = true)
    public StudySession startSmartSession(SmartDeck smartDeck, StudyDirection direction) {
        List<Card> cards = smartDeckService.cardsFor(smartDeck.conditionType(), smartDeck.param());
        return new StudySession(smartDeck.name(), direction, idsOf(cards));
    }

    @Transactional(readOnly = true)
    public StudySession startPresetSession(SmartCondition condition, StudyDirection direction) {
        List<Card> cards = smartDeckService.cardsFor(condition, null);
        return new StudySession(condition.getLabel(), direction, idsOf(cards));
    }
    // end::start[]

    @Transactional(readOnly = true)
    public long todayCount() {
        return cardRepository.countDue(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Card currentCard(StudySession session) {
        return cardRepository.findById(session.currentCardId())
                .orElseThrow(() -> new IllegalStateException(
                        "세션의 카드가 삭제됐습니다: " + session.currentCardId()));
    }

    // tag::answer[]
    /**
     * 판정 하나를 기록한다.
     * 첫 라운드의 판정만 복습 일정에 반영하고, 재도전 라운드는 기록만 남긴다.
     */
    @Transactional
    public void answer(StudySession session, boolean correct) {
        Long cardId = session.currentCardId();
        LocalDateTime now = LocalDateTime.now();

        if (!session.isRetryRound()) {
            ReviewState state = reviewStateRepository.findByCardId(cardId)
                    .orElseGet(() -> ReviewState.initial(cardId));
            ReviewState reviewed = state.reviewed(correct, now);
            if (reviewed.id() == null) {
                reviewStateRepository.insert(reviewed);
            } else {
                reviewStateRepository.update(reviewed);
            }
        }
        reviewLogRepository.insert(
                ReviewLog.of(cardId, correct, session.isRetryRound(), now));

        session.answer(correct);
    }
    // end::answer[]

    private List<Long> idsOf(List<Card> cards) {
        return cards.stream().map(Card::id).toList();
    }
}

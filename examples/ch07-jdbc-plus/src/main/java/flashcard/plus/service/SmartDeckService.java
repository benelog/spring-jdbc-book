package flashcard.plus.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.SmartCondition;
import flashcard.plus.domain.SmartDeck;
import flashcard.plus.repository.CardRepository;
import flashcard.plus.repository.SmartDeckRepository;

@Service
public class SmartDeckService {

    // "추천 복습" 타일과 스마트 덱이 함께 쓰는 기준값
    public static final int OFTEN_WRONG_MIN_ATTEMPTS = 3;
    public static final int OFTEN_WRONG_MIN_PERCENT = 40;
    public static final int STALE_DAYS = 7;
    public static final int RECENT_DAYS_DEFAULT = 7;

    private final SmartDeckRepository smartDeckRepository;
    private final CardRepository cardRepository;

    public SmartDeckService(SmartDeckRepository smartDeckRepository,
                            CardRepository cardRepository) {
        this.smartDeckRepository = smartDeckRepository;
        this.cardRepository = cardRepository;
    }

    // tag::cards-for[]
    /** 스마트 덱은 카드를 담아 두지 않는다. 학습을 시작하는 순간 조건으로 골라 온다. */
    @Transactional(readOnly = true)
    public List<Card> cardsFor(SmartCondition condition, String param) {
        return switch (condition) {
            case OFTEN_WRONG -> cardRepository.findOftenWrong(
                    OFTEN_WRONG_MIN_ATTEMPTS, OFTEN_WRONG_MIN_PERCENT);
            case STALE -> cardRepository.findStale(
                    LocalDateTime.now().minusDays(STALE_DAYS));
            case TAGGED -> cardRepository.findByTagName(param);
            case RECENT -> cardRepository.findRecent(
                    LocalDateTime.now().minusDays(parseDays(param)));
        };
    }
    // end::cards-for[]

    private int parseDays(String param) {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return RECENT_DAYS_DEFAULT;
        }
    }

    @Transactional(readOnly = true)
    public long oftenWrongCount() {
        return cardRepository.countOftenWrong(OFTEN_WRONG_MIN_ATTEMPTS, OFTEN_WRONG_MIN_PERCENT);
    }

    @Transactional(readOnly = true)
    public long staleCount() {
        return cardRepository.countStale(LocalDateTime.now().minusDays(STALE_DAYS));
    }

    @Transactional(readOnly = true)
    public List<SmartDeck> smartDecks() {
        return smartDeckRepository.findAllOrdered();
    }

    @Transactional(readOnly = true)
    public SmartDeck getSmartDeck(Long id) {
        return smartDeckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스마트 덱이 없습니다: " + id));
    }

    @Transactional
    public SmartDeck create(String name, SmartCondition condition, String param) {
        return smartDeckRepository.insert(SmartDeck.create(name, condition, param));
    }

    @Transactional
    public void delete(Long id) {
        smartDeckRepository.deleteById(id);
    }
}

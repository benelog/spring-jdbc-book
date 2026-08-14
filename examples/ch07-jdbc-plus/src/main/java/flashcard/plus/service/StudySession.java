package flashcard.plus.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// tag::class[]
/**
 * 진행 중인 학습 세션. HTTP 세션에 담기므로 Serializable이다.
 * 한 바퀴를 다 돌면 틀린 카드만 모아 재도전 라운드가 반복된다.
 */
public class StudySession implements Serializable {

    private final String title;
    private final StudyDirection direction;
    private List<Long> cardIds;
    private List<Long> wrongCardIds = new ArrayList<>();
    private int index;
    private int round = 1;

    public StudySession(String title, StudyDirection direction, List<Long> cardIds) {
        this.title = title;
        this.direction = direction;
        this.cardIds = new ArrayList<>(cardIds);
    }

    public Long currentCardId() {
        return cardIds.get(index);
    }

    public void answer(boolean correct) {
        if (!correct) {
            wrongCardIds.add(currentCardId());
        }
        index++;
    }

    public boolean isRoundFinished() {
        return index >= cardIds.size();
    }

    public boolean hasWrongCards() {
        return !wrongCardIds.isEmpty();
    }

    /** 틀린 카드만 모아 다음 라운드를 시작한다. */
    public void startRetryRound() {
        cardIds = wrongCardIds;
        wrongCardIds = new ArrayList<>();
        index = 0;
        round++;
    }

    /** 재도전 라운드의 판정은 복습 일정에 반영하지 않는다. */
    public boolean isRetryRound() {
        return round > 1;
    }
    // end::class[]

    public String getTitle() {
        return title;
    }

    public StudyDirection getDirection() {
        return direction;
    }

    public int getRound() {
        return round;
    }

    public int getPosition() {
        return Math.min(index + 1, cardIds.size());
    }

    public int getTotal() {
        return cardIds.size();
    }

    public int getWrongCount() {
        return wrongCardIds.size();
    }

    public boolean isEmpty() {
        return cardIds.isEmpty();
    }
}

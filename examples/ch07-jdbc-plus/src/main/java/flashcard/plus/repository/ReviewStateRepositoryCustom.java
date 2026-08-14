package flashcard.plus.repository;

import java.util.Optional;

import flashcard.plus.domain.ReviewState;

public interface ReviewStateRepositoryCustom {

    Optional<ReviewState> findByCardId(Long cardId);
}

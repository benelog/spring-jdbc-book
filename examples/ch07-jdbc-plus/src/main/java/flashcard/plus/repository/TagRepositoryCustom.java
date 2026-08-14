package flashcard.plus.repository;

import java.util.List;
import java.util.Optional;

import flashcard.plus.domain.CardTagName;
import flashcard.plus.domain.Tag;

public interface TagRepositoryCustom {

    Optional<Tag> findByName(String name);

    /** 덱에 속한 모든 카드의 태그를 (카드 id, 태그 이름) 쌍으로 한 번에 가져온다. */
    List<CardTagName> findTagNamesByDeckId(Long deckId);

    List<String> findTagNamesByCardId(Long cardId);

    List<String> findAllNames();

    void attach(Long cardId, Long tagId);

    void detachAll(Long cardId);
}

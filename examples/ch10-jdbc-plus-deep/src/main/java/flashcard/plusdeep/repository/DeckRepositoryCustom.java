package flashcard.plusdeep.repository;

import java.util.List;

import flashcard.plusdeep.domain.Deck;

// tag::class[]
public interface DeckRepositoryCustom {

    /** 휴지통에 들어가지 않은 덱. */
    List<Deck> findActive();

    /** 휴지통: soft delete로 지워진 덱. */
    List<Deck> findTrash();
}
// end::class[]

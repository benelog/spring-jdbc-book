package flashcard.plusdeep.repository;

import java.util.List;

import flashcard.plusdeep.domain.Card;

// tag::class[]
public interface CardRepositoryCustom {

    /** 예문에 담긴 문장까지 뒤져 카드를 찾는다. 애그리거트 전체를 쿼리 한 번으로 조립한다. */
    List<Card> searchWithExamples(String keyword);

    /** 카드 여러 장을 배치 INSERT 한 번으로 저장한다. 예문 없는 카드 행만 다룬다. */
    int[] insertBatch(List<Card> cards);
}
// end::class[]

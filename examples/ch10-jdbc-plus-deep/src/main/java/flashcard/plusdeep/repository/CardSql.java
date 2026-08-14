package flashcard.plusdeep.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

import flashcard.plusdeep.domain.Card;

// tag::class[]
public class CardSql extends SqlGeneratorSupport {

    // tag::aggregate[]
    /**
     * 애그리거트 전체(카드 + 예문)를 한 번에 읽는 SELECT.
     * aggregateColumns가 자식 테이블 컬럼까지, aggregateTables가 JOIN 절까지 만들어 준다.
     */
    public String selectByKeyword() {
        return """
                select %s
                from %s
                where card.text like :pattern
                   or card.meaning like :pattern
                order by card.id
                """.formatted(sql.aggregateColumns(Card.class), sql.aggregateTables(Card.class));
    }
    // end::aggregate[]

    // tag::insert[]
    public String insertCard() {
        return """
                insert into card (deck_id, text, meaning)
                values (:deckId, :text, :meaning)
                """;
    }
    // end::insert[]
}
// end::class[]

package flashcard.kotlin

import javax.sql.DataSource
import kotlin.jvm.optionals.getOrNull
import org.springframework.jdbc.core.simple.JdbcClient

// tag::class[]
class CardClientRepository(dataSource: DataSource) {

    private val jdbc = JdbcClient.create(dataSource)

    fun findByDeckId(deckId: Long): List<Card> =
        jdbc.sql(CardSqls.SELECT_BY_DECK)
            .param("deckId", deckId)
            .query<Card>()                     // 직접 만든 reified 확장 함수
            .list()

    /** optional()의 Java Optional은 표준 라이브러리 getOrNull()로 nullable 타입이 된다. */
    fun findById(id: Long): Card? =
        jdbc.sql(CardSqls.SELECT_BY_ID)
            .param("id", id)
            .query<Card>()
            .optional()
            .getOrNull()

    fun countByDeckId(deckId: Long): Long =
        jdbc.sql(CardSqls.COUNT_BY_DECK)
            .param("deckId", deckId)
            .query<Long>()
            .single()
}
// end::class[]

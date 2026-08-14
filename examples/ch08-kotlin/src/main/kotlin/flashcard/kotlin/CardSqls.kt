package flashcard.kotlin

// tag::object[]
/**
 * SQL을 상수로 모아 두는 object 선언.
 * 트리플 쿼트 문자열은 Java text block처럼 여러 줄 SQL을 그대로 담는다.
 * trimIndent()로 들여쓰기를 정리한다.
 */
object CardSqls {

    val SELECT_BY_DECK = """
        select id, deck_id, text, meaning
        from cards
        where deck_id = :deckId
        order by id
        """.trimIndent()

    val SELECT_BY_ID = """
        select id, deck_id, text, meaning
        from cards
        where id = :id
        """.trimIndent()

    val INSERT = """
        insert into cards (deck_id, text, meaning)
        values (:deckId, :text, :meaning)
        """.trimIndent()

    val COUNT_BY_DECK = """
        select count(*)
        from cards
        where deck_id = :deckId
        """.trimIndent()

    /** 조건이 있을 때만 where 절을 붙이는 동적 SQL. buildString도 어울린다. */
    fun search(keyword: String?): String = buildString {
        appendLine("select id, deck_id, text, meaning")
        appendLine("from cards")
        appendLine("where 1 = 1")
        if (!keyword.isNullOrBlank()) {
            appendLine("and (text like :keyword or meaning like :keyword)")
        }
        append("order by id")
    }
}
// end::object[]

package flashcard.kotlin

// tag::class[]
/**
 * Java record에 해당하는 Kotlin data class.
 * copy()로 일부 값만 바꾼 복사본을 만들 수 있고,
 * 기본값 덕분에 새 카드를 만들 때 id를 생략할 수 있다.
 */
data class Card(
    val id: Long? = null,
    val deckId: Long,
    val text: String,
    val meaning: String,
)
// end::class[]

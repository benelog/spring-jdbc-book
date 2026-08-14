package flashcard.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

// tag::class[]
class CardRepositoryTest {

    private lateinit var dataSource: EmbeddedDatabase
    private lateinit var cards: CardRepository
    private var deckId: Long = 0

    @BeforeEach
    fun setUp() {
        dataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .addScript("schema.sql")
            .build()
        cards = CardRepository(dataSource)

        val jdbc = JdbcTemplate(dataSource)
        jdbc.update("insert into decks (name) values (?)", "영어 단어장")
        deckId = jdbc.queryForObject("select max(id) from decks", Long::class.java)!!
    }

    @AfterEach
    fun tearDown() {
        dataSource.shutdown()
    }

    @Test
    fun `카드를 저장하고 조회한다`() {
        val saved = cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))

        val found = cards.findById(saved.id!!)

        assertEquals("resilient", found?.text)
        assertEquals(saved, found)   // data class는 값으로 비교된다
    }

    @Test
    fun `없는 카드는 null을 돌려준다`() {
        assertNull(cards.findById(999))
    }

    @Test
    fun `키워드로 검색한다`() {
        cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
        cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인"))

        assertEquals(1, cards.search("회복").size)
        assertEquals(2, cards.search(null).size)
    }

    @Test
    fun `원문만 뽑아 온다`() {
        cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
        cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인"))

        assertEquals(listOf("resilient", "deliberate"), cards.findTexts(deckId))
    }
}
// end::class[]

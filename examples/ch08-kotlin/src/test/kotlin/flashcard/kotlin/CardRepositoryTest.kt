package flashcard.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
    @DisplayName("카드를 저장하고 조회한다")
    fun insertAndFind() {
        val saved = cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))

        val found = cards.findById(saved.id!!)

        assertThat(found?.text).isEqualTo("resilient")
        assertThat(found).isEqualTo(saved)   // data class는 값으로 비교된다
    }

    @Test
    @DisplayName("없는 카드는 null을 돌려준다")
    fun findMissing() {
        assertThat(cards.findById(999)).isNull()
    }

    @Test
    @DisplayName("키워드로 검색한다")
    fun search() {
        cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
        cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인"))

        assertThat(cards.search("회복")).hasSize(1)
        assertThat(cards.search(null)).hasSize(2)
    }

    @Test
    @DisplayName("원문만 뽑아 온다")
    fun findTexts() {
        cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
        cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인"))

        assertThat(cards.findTexts(deckId)).containsExactly("resilient", "deliberate")
    }
}
// end::class[]

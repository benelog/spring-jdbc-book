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

class CardClientRepositoryTest {

    private lateinit var dataSource: EmbeddedDatabase
    private lateinit var cards: CardRepository
    private lateinit var clientCards: CardClientRepository
    private var deckId: Long = 0

    @BeforeEach
    fun setUp() {
        dataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .addScript("schema.sql")
            .build()
        cards = CardRepository(dataSource)
        clientCards = CardClientRepository(dataSource)

        val jdbc = JdbcTemplate(dataSource)
        jdbc.update("insert into decks (name) values (?)", "영어 단어장")
        deckId = jdbc.queryForObject("select max(id) from decks", Long::class.java)!!
    }

    @AfterEach
    fun tearDown() {
        dataSource.shutdown()
    }

    @Test
    @DisplayName("reified 확장 함수로 카드 목록과 건수를 조회한다")
    fun findAndCount() {
        cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
        cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인"))

        assertThat(clientCards.findByDeckId(deckId)).hasSize(2)
        assertThat(clientCards.countByDeckId(deckId)).isEqualTo(2)
    }

    @Test
    @DisplayName("한 건 조회는 getOrNull로 nullable 타입이 된다")
    fun findById() {
        val saved = cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))

        assertThat(clientCards.findById(saved.id!!)?.text).isEqualTo("resilient")
        assertThat(clientCards.findById(999)).isNull()
    }
}

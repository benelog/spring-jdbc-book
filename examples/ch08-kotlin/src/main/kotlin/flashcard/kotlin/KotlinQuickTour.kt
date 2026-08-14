package flashcard.kotlin

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

/**
 * Kotlin에서 Spring JDBC를 쓰는 실행 예제.
 * 실행: ./gradlew :ch08-kotlin:run
 */
// tag::main[]
fun main() {
    val dataSource = createDataSource()
    ResourceDatabasePopulator(ClassPathResource("schema.sql")).execute(dataSource)

    val deckId = insertDeck(dataSource, "영어 단어장")
    val cards = CardRepository(dataSource)

    val saved = cards.insert(Card(deckId = deckId, text = "resilient", meaning = "회복력 있는"))
    cards.insert(Card(deckId = deckId, text = "deliberate", meaning = "의도적인, 신중한"))

    println("저장된 카드: $saved")
    println("전체 카드 수: ${cards.countAll()}")
    println("'회복' 검색: ${cards.search("회복")}")
    println("원문 목록: ${cards.findTexts(deckId)}")
}
// end::main[]

fun createDataSource(): DataSource =
    HikariDataSource(HikariConfig().apply {
        jdbcUrl = "jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE"
        username = "sa"
    })

fun insertDeck(dataSource: DataSource, name: String): Long {
    val jdbc = JdbcTemplate(dataSource)
    jdbc.update("insert into decks (name) values (?)", name)
    return jdbc.queryForObject("select max(id) from decks", Long::class.java)!!
}

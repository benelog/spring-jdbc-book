package flashcard.kotlin

import javax.sql.DataSource
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.set
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert

// tag::class[]
class CardRepository(dataSource: DataSource) {

    private val jdbc: NamedParameterJdbcOperations = NamedParameterJdbcTemplate(dataSource)
    private val plainJdbc: JdbcOperations = JdbcTemplate(dataSource)
    private val cardInsert = SimpleJdbcInsert(dataSource)
        .withTableName("cards")
        .usingGeneratedKeyColumns("id")

    /** DataClassRowMapper는 Kotlin data class 생성자에도 값을 넣어 준다. */
    private val cardMapper = DataClassRowMapper.newInstance(Card::class.java)

    // tag::insert[]
    fun insert(card: Card): Card {
        val id = cardInsert.executeAndReturnKey(BeanPropertySqlParameterSource(card))
        return card.copy(id = id.toLong())   // data class의 copy로 id만 채운 복사본
    }
    // end::insert[]

    // tag::find[]
    fun findByDeckId(deckId: Long): List<Card> =
        jdbc.query(CardSqls.SELECT_BY_DECK, mapOf("deckId" to deckId), cardMapper)

    /** 없으면 null. 예외 대신 Kotlin의 nullable 타입으로 표현한다. */
    fun findById(id: Long): Card? =
        jdbc.query(CardSqls.SELECT_BY_ID, mapOf("id" to id), cardMapper).firstOrNull()
    // end::find[]

    // tag::reified[]
    /** JdbcTemplate의 Kotlin 확장 함수: 반환 타입을 제네릭 인자로만 알려 주면 된다. */
    fun countAll(): Long =
        plainJdbc.queryForObject<Long>("select count(*) from cards") ?: 0
    // end::reified[]

    // tag::operator[]
    fun search(keyword: String?): List<Card> {
        val params = MapSqlParameterSource()
        if (!keyword.isNullOrBlank()) {
            params["keyword"] = "%$keyword%"   // 확장 함수 set 덕분에 대입 문법이 된다
        }
        return jdbc.query(CardSqls.search(keyword), params, cardMapper)
    }
    // end::operator[]

    // tag::lambda[]
    /** RowMapper는 후행 람다로 자연스럽게 적는다. */
    fun findTexts(deckId: Long): List<String> =
        jdbc.query(CardSqls.SELECT_BY_DECK, mapOf("deckId" to deckId)) { rs, _ ->
            rs.getString("text")
        }
    // end::lambda[]
}
// end::class[]

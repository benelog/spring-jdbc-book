package flashcard.plusdeep.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.navercorp.spring.data.jdbc.plus.support.convert.SqlProvider;

import flashcard.plusdeep.domain.Card;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-deep-sql;DB_CLOSE_DELAY=-1"
})
class GeneratedSqlTest {

    @Autowired
    SqlProvider sqlProvider;

    // tag::aggregate-sql[]
    @Test
    @DisplayName("aggregateColumns와 aggregateTables가 자식 테이블까지 포함한 SQL 조각을 만든다")
    void aggregateSqlFragments() {
        // 자식 컬럼은 "EXAMPLES_" 접두사가 붙은 별칭으로 생성된다
        assertThat(sqlProvider.aggregateColumns(Card.class))
                .contains("\"CARD\".\"TEXT\" AS \"TEXT\"")
                .contains("\"examples\".\"SENTENCE\" AS \"EXAMPLES_SENTENCE\"");

        // FROM 절에는 자식 테이블로의 LEFT OUTER JOIN이 들어간다
        assertThat(sqlProvider.aggregateTables(Card.class))
                .contains("\"CARD\" LEFT OUTER JOIN \"CARD_EXAMPLE\" \"examples\"")
                .contains("ON \"examples\".\"CARD_ID\" = \"CARD\".\"ID\"");
    }
    // end::aggregate-sql[]
}

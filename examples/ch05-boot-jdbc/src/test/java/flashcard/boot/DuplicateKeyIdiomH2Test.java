package flashcard.boot;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 2장의 DuplicateKeyException 관용구를 H2에서, 트랜잭션 안에서 실행한다.
 * H2는 실패한 문장이 트랜잭션을 중단시키지 않으므로 뒤의 조회가 그대로 이어진다.
 */
// tag::h2[]
@JdbcTest
@Sql(statements = "create table if not exists unique_names (name varchar(100) primary key)")
class DuplicateKeyIdiomH2Test {

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Test
    @DisplayName("H2에서는 트랜잭션 안에서 실패한 문장 뒤에도 조회가 이어진다")
    void continuesAfterFailedStatement() {
        insert("resilient");

        assertThatThrownBy(() -> insert("resilient"))
                .isInstanceOf(DuplicateKeyException.class);

        assertThat(count()).isEqualTo(1);
    }

    void insert(String name) {
        jdbc.update("insert into unique_names (name) values (:name)", Map.of("name", name));
    }

    long count() {
        return jdbc.queryForObject("select count(*) from unique_names", Map.of(), Long.class);
    }
}
// end::h2[]

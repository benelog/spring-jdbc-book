package flashcard.boot;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 2장의 DuplicateKeyException 관용구를 PostgreSQL에서, 트랜잭션 안에서 실행한다.
 * PostgreSQL은 문장 하나가 실패하면 트랜잭션 전체를 중단 상태로 만든다.
 */
// tag::postgres[]
@Testcontainers(disabledWithoutDocker = true)
@JdbcTest
@Sql(statements = "create table if not exists unique_names (name varchar(100) primary key)")
class DuplicateKeyIdiomPostgresTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    NamedParameterJdbcTemplate jdbc;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("PostgreSQL에서는 트랜잭션 안에서 실패한 문장 뒤의 조회가 거부된다")
    void transactionIsAbortedAfterFailedStatement() {
        insert("resilient");

        assertThatThrownBy(() -> insert("resilient"))
                .isInstanceOf(DuplicateKeyException.class);

        assertThatThrownBy(this::count)
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("current transaction is aborted");
    }
    // end::postgres[]

    // tag::nested[]
    @Test
    @DisplayName("저장점으로 실패한 문장만 되돌리면 관용구가 다시 동작한다")
    void savepointKeepsTransactionUsable() {
        insert("resilient");

        TransactionTemplate nested = new TransactionTemplate(transactionManager);
        nested.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
        try {
            nested.executeWithoutResult(status -> insert("resilient"));
        } catch (DuplicateKeyException e) {
            // 이미 있으면 기존 것을 쓴다
        }

        assertThat(count()).isEqualTo(1);
    }
    // end::nested[]

    void insert(String name) {
        jdbc.update("insert into unique_names (name) values (:name)", Map.of("name", name));
    }

    long count() {
        return jdbc.queryForObject("select count(*) from unique_names", Map.of(), Long.class);
    }
}

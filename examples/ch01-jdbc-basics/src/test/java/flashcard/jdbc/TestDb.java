package flashcard.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/** 테스트마다 독립적인 in-memory H2 DB를 만든다. */
public class TestDb {

    // tag::in-memory[]
    public static DataSource newInMemoryDb(String name) {
        DataSource dataSource =
                DataSources.h2DataSource("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("drop all objects");
            SchemaInitializer.initSchema(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("테스트 DB 초기화 실패", e);
        }
        return dataSource;
    }
    // end::in-memory[]
}

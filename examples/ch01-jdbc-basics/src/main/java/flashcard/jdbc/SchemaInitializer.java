package flashcard.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** classpath의 schema.sql을 실행해 테이블을 만든다. */
public class SchemaInitializer {

    // tag::init[]
    public static void initSchema(Connection connection) throws SQLException {
        String schema = readClasspathFile("schema.sql");
        try (Statement statement = connection.createStatement()) {
            statement.execute(schema);
        }
    }
    // end::init[]

    private static String readClasspathFile(String path) {
        try (InputStream in = SchemaInitializer.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("classpath에 파일이 없습니다: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

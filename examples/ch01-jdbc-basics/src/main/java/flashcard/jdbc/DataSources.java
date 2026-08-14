package flashcard.jdbc;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.h2.jdbcx.JdbcDataSource;

/** 이 장의 예제들이 함께 쓰는 DataSource 생성 코드. */
public class DataSources {

    public static final String FILE_DB_URL = "jdbc:h2:./db/flashcard;AUTO_SERVER=TRUE";

    // tag::h2[]
    public static DataSource h2DataSource(String url) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(url);
        dataSource.setUser("sa");
        return dataSource;
    }
    // end::h2[]

    // tag::hikari[]
    public static DataSource pooledDataSource(String url) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("sa");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
    // end::hikari[]
}

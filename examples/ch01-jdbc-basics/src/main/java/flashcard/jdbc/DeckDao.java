package flashcard.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class DeckDao {

    private final DataSource dataSource;

    public DeckDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long insert(String name) throws SQLException {
        String sql = "insert into decks (name) values (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public List<Deck> findAll() throws SQLException {
        String sql = "select id, name from decks order by id";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Deck> decks = new ArrayList<>();
            while (resultSet.next()) {
                decks.add(new Deck(resultSet.getLong("id"), resultSet.getString("name")));
            }
            return decks;
        }
    }
}

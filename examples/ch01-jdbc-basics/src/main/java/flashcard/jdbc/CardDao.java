package flashcard.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * 순수 JDBC API만으로 작성한 카드 DAO.
 * 자원 정리와 예외 처리를 전부 직접 감당해야 한다는 점을 보여 준다.
 */
public class CardDao {

    private final DataSource dataSource;

    public CardDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // tag::insert[]
    public long insert(Card card) throws SQLException {
        String sql = "insert into cards (deck_id, text, meaning) values (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, card.deckId());
            statement.setString(2, card.text());
            statement.setString(3, card.meaning());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }
    // end::insert[]

    // tag::select[]
    public List<Card> findByDeckId(long deckId) throws SQLException {
        String sql = "select id, deck_id, text, meaning from cards where deck_id = ? order by id";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, deckId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Card> cards = new ArrayList<>();
                while (resultSet.next()) {
                    cards.add(mapCard(resultSet));
                }
                return cards;
            }
        }
    }
    // end::select[]

    // tag::select-one[]
    public Optional<Card> findById(long id) throws SQLException {
        String sql = "select id, deck_id, text, meaning from cards where id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCard(resultSet));
                }
                return Optional.empty();
            }
        }
    }
    // end::select-one[]

    // tag::mapping[]
    private Card mapCard(ResultSet resultSet) throws SQLException {
        return new Card(
                resultSet.getLong("id"),
                resultSet.getLong("deck_id"),
                resultSet.getString("text"),
                resultSet.getString("meaning")
        );
    }
    // end::mapping[]

    // tag::update[]
    public int update(Card card) throws SQLException {
        String sql = "update cards set text = ?, meaning = ? where id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, card.text());
            statement.setString(2, card.meaning());
            statement.setLong(3, card.id());
            return statement.executeUpdate();
        }
    }
    // end::update[]

    public int deleteById(long id) throws SQLException {
        String sql = "delete from cards where id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate();
        }
    }

    // tag::batch[]
    public int[] insertAll(long deckId, List<Card> cards) throws SQLException {
        String sql = "insert into cards (deck_id, text, meaning) values (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Card card : cards) {
                statement.setLong(1, deckId);
                statement.setString(2, card.text());
                statement.setString(3, card.meaning());
                statement.addBatch();
            }
            return statement.executeBatch();
        }
    }
    // end::batch[]
}

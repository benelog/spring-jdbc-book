package flashcard.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

/**
 * 덱 하나와 그 안의 카드들을 한 트랜잭션으로 저장한다.
 * 카드 하나라도 실패하면 덱까지 함께 되돌린다.
 */
public class DeckImporter {

    private final DataSource dataSource;

    public DeckImporter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // tag::tx[]
    public long importDeck(String deckName, List<Card> cards) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);      // 트랜잭션 시작

            long deckId = insertDeck(connection, deckName);
            for (Card card : cards) {
                insertCard(connection, deckId, card);
            }

            connection.commit();                  // 전부 성공하면 확정
            return deckId;
        } catch (SQLException e) {
            connection.rollback();                // 하나라도 실패하면 전부 취소
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }
    // end::tx[]

    private long insertDeck(Connection connection, String name) throws SQLException {
        String sql = "insert into decks (name) values (?)";
        try (PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void insertCard(Connection connection, long deckId, Card card) throws SQLException {
        String sql = "insert into cards (deck_id, text, meaning) values (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, deckId);
            statement.setString(2, card.text());
            statement.setString(3, card.meaning());
            statement.executeUpdate();
        }
    }
}

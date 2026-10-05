package dao;

import model.QueueEntry;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for queue_entries.
 *
 * Responsible for:
 * - Adding customers to the queue
 * - Finding waiting customers
 * - Calling the next customer
 * - Updating queue status
 * - Counting waiting customers
 * - Maintaining queue positions
 */
public class QueueDAO {

    /**
     * Adds a new queue entry.
     */
    public long addToQueue(QueueEntry entry) throws SQLException {

        String sql = """
                INSERT INTO queue_entries
                (token_id, queue_position, effective_priority, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, entry.getTokenId());
            statement.setInt(2, entry.getQueuePosition());
            statement.setBigDecimal(
                    3,
                    BigDecimal.valueOf(entry.getEffectivePriority()));
            statement.setString(4, entry.getStatus().name());

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    entry.setQueueEntryId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Queue entry could not be created.");
    }

    /**
     * Returns number of customers currently waiting.
     */
    public int countWaiting() throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM queue_entries
                WHERE status = 'WAITING'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Finds all waiting customers in queue order.
     */
    public List<QueueEntry> findWaitingEntries() throws SQLException {

        String sql = """
                SELECT queue_entry_id,
                       token_id,
                       queue_position,
                       effective_priority,
                       joined_at,
                       last_priority_update,
                       status
                FROM queue_entries
                WHERE status = 'WAITING'
                ORDER BY effective_priority DESC,
                         joined_at ASC,
                         queue_position ASC
                """;

        List<QueueEntry> entries = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                entries.add(mapRow(rs));
            }
        }

        return entries;
    }

    /**
     * Finds the next customer who should be served.
     *
     * Priority is:
     * 1. Higher effective priority
     * 2. Earlier arrival
     * 3. Earlier queue position
     */
    public QueueEntry findNextWaiting() throws SQLException {

        String sql = """
                SELECT queue_entry_id,
                       token_id,
                       queue_position,
                       effective_priority,
                       joined_at,
                       last_priority_update,
                       status
                FROM queue_entries
                WHERE status = 'WAITING'
                ORDER BY effective_priority DESC,
                         joined_at ASC,
                         queue_position ASC
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return mapRow(rs);
            }
        }

        return null;
    }

    /**
     * Updates queue entry status.
     */
    public boolean updateStatus(long tokenId,
                                QueueEntry.Status status)
            throws SQLException {

        String sql = """
                UPDATE queue_entries
                SET status = ?,
                    last_priority_update = CURRENT_TIMESTAMP
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setLong(2, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Updates the queue position of a token.
     */
    public boolean updatePosition(long tokenId,
                                  int position)
            throws SQLException {

        String sql = """
                UPDATE queue_entries
                SET queue_position = ?
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, position);
            statement.setLong(2, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Removes a token from the active queue.
     */
    public boolean removeFromQueue(long tokenId)
            throws SQLException {

        String sql = """
                DELETE FROM queue_entries
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Gets the current position of a waiting token.
     */
    public int getPosition(long tokenId)
            throws SQLException {

        String sql = """
                SELECT queue_position
                FROM queue_entries
                WHERE token_id = ?
                  AND status = 'WAITING'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("queue_position");
                }
            }
        }

        return -1;
    }

    /**
     * Recalculates positions of waiting customers.
     *
     * This makes the queue display accurate after
     * somebody is called/completed/cancelled.
     */
    public void refreshPositions() throws SQLException {

        List<Long> tokenIds = new ArrayList<>();

        String selectSql = """
                SELECT token_id
                FROM queue_entries
                WHERE status = 'WAITING'
                ORDER BY effective_priority DESC,
                         joined_at ASC,
                         queue_position ASC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement select =
                     connection.prepareStatement(selectSql);
             ResultSet rs = select.executeQuery()) {

            while (rs.next()) {
                tokenIds.add(rs.getLong("token_id"));
            }
        }

        String updateSql = """
                UPDATE queue_entries
                SET queue_position = ?
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement update =
                     connection.prepareStatement(updateSql)) {

            int position = 1;

            for (Long tokenId : tokenIds) {
                update.setInt(1, position++);
                update.setLong(2, tokenId);
                update.addBatch();
            }

            update.executeBatch();
        }
    }

    /**
     * Converts database row into QueueEntry object.
     */
    private QueueEntry mapRow(ResultSet rs)
            throws SQLException {

        QueueEntry entry = new QueueEntry();

        entry.setQueueEntryId(
                rs.getLong("queue_entry_id"));

        entry.setTokenId(
                rs.getLong("token_id"));

        entry.setQueuePosition(
                rs.getInt("queue_position"));

        BigDecimal priority =
                rs.getBigDecimal("effective_priority");

        entry.setEffectivePriority(
                priority == null
                        ? 0.0
                        : priority.doubleValue());

        Timestamp joined =
                rs.getTimestamp("joined_at");

        if (joined != null) {
            entry.setJoinedAt(
                    joined.toLocalDateTime());
        }

        Timestamp lastUpdate =
                rs.getTimestamp("last_priority_update");

        if (lastUpdate != null) {
            entry.setLastPriorityUpdate(
                    lastUpdate.toLocalDateTime());
        }

        String status =
                rs.getString("status");

        if (status != null) {
            entry.setStatus(
                    QueueEntry.Status.valueOf(
                            status.toUpperCase()));
        }

        return entry;
    }
}
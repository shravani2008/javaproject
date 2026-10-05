package dao;

import model.Token;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for token management.
 */
public class TokenDAO {

    /**
     * Creates a new token in the database.
     */
    public long createToken(Token token) throws SQLException {

        String sql = """
                INSERT INTO tokens
                (token_code, customer_id, service_id,
                 counter_id, status, priority_level,
                 arrival_time, estimated_wait_minutes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, token.getTokenCode());
            statement.setInt(2, token.getCustomerId());
            statement.setInt(3, token.getServiceId());

            if (token.getCounterId() == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, token.getCounterId());
            }

            statement.setString(5, token.getStatus().name());
            statement.setString(6, token.getPriorityLevel().name());

            statement.setTimestamp(
                    7,
                    Timestamp.valueOf(token.getArrivalTime()));

            if (token.getEstimatedWaitMinutes() == null) {
                statement.setNull(8, Types.DECIMAL);
            } else {
                statement.setBigDecimal(
                        8,
                        BigDecimal.valueOf(
                                token.getEstimatedWaitMinutes()));
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    token.setTokenId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Token could not be created.");
    }

    /**
     * Updates token status.
     */
    public boolean updateStatus(long tokenId,
                                Token.Status status)
            throws SQLException {

        String sql = """
                UPDATE tokens
                SET status = ?
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setLong(2, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Marks token as called.
     */
    public boolean markCalled(long tokenId)
            throws SQLException {

        String sql = """
                UPDATE tokens
                SET status = 'CALLED',
                    called_time = CURRENT_TIMESTAMP
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Marks token as currently being served.
     */
    public boolean markServing(long tokenId)
            throws SQLException {

        String sql = """
                UPDATE tokens
                SET status = 'SERVING',
                    service_start_time = CURRENT_TIMESTAMP
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Marks token as completed and calculates
     * actual waiting/service time.
     */
    public boolean markCompleted(long tokenId)
            throws SQLException {

        String sql = """
                UPDATE tokens
                SET status = 'COMPLETED',
                    completion_time = CURRENT_TIMESTAMP,
                    actual_wait_minutes =
                        CASE
                            WHEN service_start_time IS NOT NULL
                            THEN TIMESTAMPDIFF(
                                SECOND,
                                arrival_time,
                                service_start_time
                            ) / 60.0
                            ELSE NULL
                        END,
                    actual_service_minutes =
                        CASE
                            WHEN service_start_time IS NOT NULL
                            THEN TIMESTAMPDIFF(
                                SECOND,
                                service_start_time,
                                CURRENT_TIMESTAMP
                            ) / 60.0
                            ELSE NULL
                        END
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds token by ID.
     */
    public Token findById(long tokenId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM tokens
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds token by token code.
     */
    public Token findByCode(String tokenCode)
            throws SQLException {

        String sql = """
                SELECT *
                FROM tokens
                WHERE token_code = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    tokenCode.trim().toUpperCase());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    /**
     * Returns all active tokens.
     */
    public List<Token> findActiveTokens()
            throws SQLException {

        String sql = """
                SELECT *
                FROM tokens
                WHERE status IN
                    ('GENERATED','WAITING','CALLED','SERVING')
                ORDER BY arrival_time ASC
                """;

        List<Token> tokens = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                tokens.add(mapRow(rs));
            }
        }

        return tokens;
    }

    /**
     * Updates estimated waiting time.
     */
    public boolean updateEstimatedWait(long tokenId,
                                       double minutes)
            throws SQLException {

        String sql = """
                UPDATE tokens
                SET estimated_wait_minutes = ?
                WHERE token_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    BigDecimal.valueOf(minutes));

            statement.setLong(2, tokenId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Generates next token code.
     *
     * Example:
     * T001, T002, T003...
     */
    public String generateNextTokenCode()
            throws SQLException {

        String sql = """
                SELECT MAX(
                    CAST(
                        SUBSTRING(token_code, 2)
                        AS UNSIGNED
                    )
                )
                FROM tokens
                WHERE token_code LIKE 'T%'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                int maximum = rs.getInt(1);

                if (rs.wasNull()) {
                    maximum = 0;
                }

                return String.format(
                        "T%03d",
                        maximum + 1);
            }
        }

        return "T001";
    }

    /**
     * Converts database row into Token.
     */
    private Token mapRow(ResultSet rs)
            throws SQLException {

        Token token = new Token();

        token.setTokenId(
                rs.getLong("token_id"));

        token.setTokenCode(
                rs.getString("token_code"));

        token.setCustomerId(
                rs.getInt("customer_id"));

        token.setServiceId(
                rs.getInt("service_id"));

        int counterId =
                rs.getInt("counter_id");

        if (!rs.wasNull()) {
            token.setCounterId(counterId);
        }

        String status =
                rs.getString("status");

        if (status != null) {
            token.setStatus(
                    Token.Status.valueOf(
                            status.toUpperCase()));
        }

        String priority =
                rs.getString("priority_level");

        if (priority != null) {
            token.setPriorityLevel(
                    Token.PriorityLevel.valueOf(
                            priority.toUpperCase()));
        }

        Timestamp arrival =
                rs.getTimestamp("arrival_time");

        if (arrival != null) {
            token.setArrivalTime(
                    arrival.toLocalDateTime());
        }

        Timestamp called =
                rs.getTimestamp("called_time");

        if (called != null) {
            token.setCalledTime(
                    called.toLocalDateTime());
        }

        Timestamp start =
                rs.getTimestamp("service_start_time");

        if (start != null) {
            token.setServiceStartTime(
                    start.toLocalDateTime());
        }

        Timestamp completion =
                rs.getTimestamp("completion_time");

        if (completion != null) {
            token.setCompletionTime(
                    completion.toLocalDateTime());
        }

        BigDecimal estimated =
                rs.getBigDecimal(
                        "estimated_wait_minutes");

        if (estimated != null) {
            token.setEstimatedWaitMinutes(
                    estimated.doubleValue());
        }

        BigDecimal actualWait =
                rs.getBigDecimal(
                        "actual_wait_minutes");

        if (actualWait != null) {
            token.setActualWaitMinutes(
                    actualWait.doubleValue());
        }

        BigDecimal actualService =
                rs.getBigDecimal(
                        "actual_service_minutes");

        if (actualService != null) {
            token.setActualServiceMinutes(
                    actualService.doubleValue());
        }

        return token;
    }
    /**
 * Returns the token currently being served.
 * Used by Customer Dashboard to display live queue status.
 */
public Token findCurrentlyServing() {

    String sql = """
            SELECT *
            FROM tokens
            WHERE status = 'SERVING'
            ORDER BY service_start_time ASC
            LIMIT 1
            """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            return mapRow(resultSet);
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "Unable to find currently serving token: "
                        + e.getMessage(), e);
    }

    return null;
}
/**
 * Counts tokens by status.
 */
public int countByStatus(Token.Status status)
        throws SQLException {

    String sql = """
            SELECT COUNT(*)
            FROM tokens
            WHERE status = ?
            """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, status.name());

        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }

    return 0;
}

/**
 * Finds all tokens belonging to a customer.
 */
public List<Token> findByCustomerId(int customerId)
        throws SQLException {

    String sql = """
            SELECT *
            FROM tokens
            WHERE customer_id = ?
            ORDER BY arrival_time DESC
            """;

    List<Token> tokens = new ArrayList<>();

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, customerId);

        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                tokens.add(mapRow(rs));
            }
        }
    }

    return tokens;
}

/**
 * Finds active tokens for a particular service.
 */
public List<Token> findActiveByServiceId(int serviceId)
        throws SQLException {

    String sql = """
            SELECT *
            FROM tokens
            WHERE service_id = ?
              AND status IN
                  ('GENERATED','WAITING','CALLED','SERVING')
            ORDER BY arrival_time ASC
            """;

    List<Token> tokens = new ArrayList<>();

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, serviceId);

        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                tokens.add(mapRow(rs));
            }
        }
    }

    return tokens;
}

}

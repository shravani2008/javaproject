package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized JDBC connection manager for the Smart Queue
 * Management and Waiting-Time Optimization System.
 *
 * Responsibilities:
 * - Maintain database configuration
 * - Establish JDBC connections
 * - Verify database connectivity
 * - Close database resources safely
 *
 * MySQL database:
 * smart_queue_db
 */
public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/smart_queue_db"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=Asia/Kolkata";

    /*
     * Change these according to your MySQL installation.
     */
    private static final String USERNAME = "root";

    private static final String PASSWORD = "shravani2008";

    private DBConnection() {
        // Prevent object creation.
    }

    /**
     * Establishes a connection with the MySQL database.
     *
     * @return active database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {

            throw new SQLException(
                    "MySQL JDBC Driver was not found. "
                            + "Make sure mysql-connector-j is available "
                            + "in the project classpath.",
                    exception
            );
        }

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }

    /**
     * Tests whether the database connection can be established.
     *
     * @return true if connection succeeds, otherwise false
     */
    public static boolean testConnection() {

        try (Connection connection = getConnection()) {

            return connection != null
                    && !connection.isClosed();

        } catch (SQLException exception) {

            System.err.println(
                    "Database connection failed: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    /**
     * Safely closes a JDBC connection.
     *
     * @param connection connection to close
     */
    public static void close(Connection connection) {

        if (connection != null) {

            try {
                connection.close();

            } catch (SQLException exception) {

                System.err.println(
                        "Unable to close database connection: "
                                + exception.getMessage()
                );
            }
        }
    }
}
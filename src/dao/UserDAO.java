package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for the users table.
 *
 * Handles:
 * - User authentication
 * - User lookup
 * - User creation
 * - User activation/deactivation
 */
public class UserDAO {

    /**
     * Authenticate a user using username and password hash.
     *
     * @param username username entered during login
     * @param passwordHash hashed password
     * @return User object if authentication succeeds, otherwise null
     */
    public User authenticate(String username, String passwordHash) {

        String sql = """
                SELECT user_id,
                       username,
                       password_hash,
                       full_name,
                       email,
                       phone,
                       role,
                       is_active
                FROM users
                WHERE username = ?
                  AND password_hash = ?
                  AND is_active = 1
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to authenticate user: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Find user by username.
     */
    public User findByUsername(String username) {

        String sql = """
                SELECT user_id,
                       username,
                       password_hash,
                       full_name,
                       email,
                       phone,
                       role,
                       is_active
                FROM users
                WHERE username = ?
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find user: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Find user by ID.
     */
    public User findById(int userId) {

        String sql = """
                SELECT user_id,
                       username,
                       password_hash,
                       full_name,
                       email,
                       phone,
                       role,
                       is_active
                FROM users
                WHERE user_id = ?
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find user by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create a new user.
     *
     * @return generated user ID
     */
    public int create(User user) {

        String sql = """
                INSERT INTO users
                (
                    username,
                    password_hash,
                    full_name,
                    email,
                    phone,
                    role,
                    is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getRole().name());
            statement.setBoolean(7, user.isActive());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("User creation failed.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to create user: " + e.getMessage(), e);
        }

        return -1;
    }

    /**
     * Update user's active status.
     */
    public boolean updateActiveStatus(int userId, boolean active) {

        String sql = """
                UPDATE users
                SET is_active = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, active);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update user status: " + e.getMessage(), e);
        }
    }

    /**
     * Convert database row into User object.
     */
    private User mapUser(ResultSet resultSet) throws SQLException {

        User user = new User();

        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));

        String roleValue = resultSet.getString("role");

        if (roleValue != null && !roleValue.isBlank()) {
            user.setRole(
                    User.Role.valueOf(
                            roleValue.trim().toUpperCase()
                    )
            );
        }

        user.setActive(resultSet.getBoolean("is_active"));

        return user;
    }
}
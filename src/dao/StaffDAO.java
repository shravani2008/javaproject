package dao;

import model.Staff;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for staff-related database operations.
 */
public class StaffDAO {

    /**
     * Find staff using user ID.
     */
    public Staff findByUserId(int userId) {

        String sql = """
                SELECT
                    s.staff_id,
                    s.user_id,
                    s.employee_code,
                    s.department,

                    u.username,
                    u.password_hash,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.is_active

                FROM staff s

                INNER JOIN users u
                    ON s.user_id = u.user_id

                WHERE s.user_id = ?

                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapStaff(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find staff: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Find staff using staff ID.
     */
    public Staff findById(int staffId) {

        String sql = """
                SELECT
                    s.staff_id,
                    s.user_id,
                    s.employee_code,
                    s.department,

                    u.username,
                    u.password_hash,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.is_active

                FROM staff s

                INNER JOIN users u
                    ON s.user_id = u.user_id

                WHERE s.staff_id = ?

                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, staffId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapStaff(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find staff by ID: "
                            + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create staff profile after creating user.
     */
    public int create(Staff staff) {

        String sql = """
                INSERT INTO staff
                (
                    user_id,
                    employee_code,
                    department
                )
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(
                    1,
                    staff.getUserId()
            );

            statement.setString(
                    2,
                    staff.getEmployeeCode()
            );

            statement.setString(
                    3,
                    staff.getDepartment()
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int staffId = keys.getInt(1);

                    staff.setStaffId(staffId);

                    return staffId;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to create staff: "
                            + e.getMessage(), e);
        }

        return -1;
    }

    /**
     * Convert database row into Staff object.
     */
    private Staff mapStaff(ResultSet resultSet)
            throws SQLException {

        return new Staff(
                resultSet.getInt("user_id"),
                resultSet.getInt("staff_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("employee_code"),
                resultSet.getString("department"),
                resultSet.getBoolean("is_active")
        );
    }
}
package dao;

import model.Customer;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for customer-related database operations.
 */
public class CustomerDAO {

    /**
     * Find customer using the associated user ID.
     */
    public Customer findByUserId(int userId) {

        String sql = """
                SELECT
                    c.customer_id,
                    c.user_id,
                    c.customer_type,
                    c.date_of_birth,

                    u.username,
                    u.password_hash,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.is_active

                FROM customers c

                INNER JOIN users u
                    ON c.user_id = u.user_id

                WHERE c.user_id = ?

                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCustomer(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find customer: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Find customer using customer ID.
     */
    public Customer findById(int customerId) {

        String sql = """
                SELECT
                    c.customer_id,
                    c.user_id,
                    c.customer_type,
                    c.date_of_birth,

                    u.username,
                    u.password_hash,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.is_active

                FROM customers c

                INNER JOIN users u
                    ON c.user_id = u.user_id

                WHERE c.customer_id = ?

                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCustomer(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find customer by ID: "
                            + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create customer profile after creating a user.
     */
    public int create(Customer customer) {

        String sql = """
                INSERT INTO customers
                (
                    user_id,
                    customer_type,
                    date_of_birth
                )
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, customer.getUserId());

            statement.setString(
                    2,
                    customer.getCustomerType().name()
            );

            if (customer.getDateOfBirth() != null) {
                statement.setDate(
                        3,
                        java.sql.Date.valueOf(
                                customer.getDateOfBirth()
                        )
                );
            } else {
                statement.setNull(
                        3,
                        java.sql.Types.DATE
                );
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    int customerId = keys.getInt(1);

                    customer.setCustomerId(customerId);

                    return customerId;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to create customer: "
                            + e.getMessage(), e);
        }

        return -1;
    }

    /**
     * Update customer type.
     *
     * Used for NORMAL / PRIORITY queue handling.
     */
    public boolean updateCustomerType(
            int customerId,
            Customer.CustomerType customerType) {

        String sql = """
                UPDATE customers
                SET customer_type = ?
                WHERE customer_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    customerType.name()
            );

            statement.setInt(2, customerId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update customer type: "
                            + e.getMessage(), e);
        }
    }

    /**
     * Convert database row into Customer object.
     */
    private Customer mapCustomer(ResultSet resultSet)
            throws SQLException {

        Customer.CustomerType type =
                Customer.CustomerType.valueOf(
                        resultSet
                                .getString("customer_type")
                                .toUpperCase()
                );

        java.sql.Date dob =
                resultSet.getDate("date_of_birth");

        return new Customer(
                resultSet.getInt("user_id"),
                resultSet.getInt("customer_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                type,
                dob == null ? null : dob.toLocalDate(),
                resultSet.getBoolean("is_active")
        );
    }
}
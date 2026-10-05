package dao;

import model.Appointment;
import model.Appointment.Status;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Statement;

/**
 * Data Access Object for appointment records.
 *
 * Handles database operations related to customer appointments.
 */
public class AppointmentDAO {

    /**
     * Finds an appointment by its ID.
     *
     * @param appointmentId appointment ID
     * @return appointment if found, otherwise null
     * @throws SQLException if database operation fails
     */
    public Appointment findById(long appointmentId)
            throws SQLException {

        String sql = """
                SELECT appointment_id,
                       customer_id,
                       service_id,
                       appointment_date,
                       appointment_time,
                       status,
                       token_id
                FROM appointments
                WHERE appointment_id = ?
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, appointmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAppointment(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Creates a new appointment.
     *
     * @param appointment appointment to save
     * @return generated appointment ID
     * @throws SQLException if insertion fails
     */
    public long save(Appointment appointment)
            throws SQLException {

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment cannot be null."
            );
        }

        String sql = """
                INSERT INTO appointments
                (
                    customer_id,
                    service_id,
                    appointment_date,
                    appointment_time,
                    status,
                    token_id
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(
                    1,
                    appointment.getCustomerId()
            );

            statement.setInt(
                    2,
                    appointment.getServiceId()
            );

            statement.setDate(
                    3,
                    Date.valueOf(
                            appointment.getAppointmentDate()
                    )
            );

            statement.setTime(
                    4,
                    Time.valueOf(
                            appointment.getAppointmentTime()
                    )
            );

            statement.setString(
                    5,
                    appointment.getStatus().name()
            );

            if (appointment.getTokenId() != null) {

                statement.setLong(
                        6,
                        appointment.getTokenId()
                );

            } else {

                statement.setNull(
                        6,
                        java.sql.Types.BIGINT
                );
            }

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Creating appointment failed. "
                                + "No record was inserted."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    long appointmentId =
                            generatedKeys.getLong(1);

                    appointment.setAppointmentId(
                            appointmentId
                    );

                    return appointmentId;
                }
            }
        }

        throw new SQLException(
                "Creating appointment failed. "
                        + "No generated appointment ID was returned."
        );
    }

    /**
     * Updates appointment status.
     *
     * @param appointmentId appointment ID
     * @param status new appointment status
     * @return true if updated successfully
     * @throws SQLException if database operation fails
     */
    public boolean updateStatus(
            long appointmentId,
            Status status
    ) throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Appointment status cannot be null."
            );
        }

        String sql = """
                UPDATE appointments
                SET status = ?
                WHERE appointment_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    status.name()
            );

            statement.setLong(
                    2,
                    appointmentId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Assigns a token to an appointment.
     *
     * @param appointmentId appointment ID
     * @param tokenId token ID
     * @return true if assignment succeeded
     * @throws SQLException if database operation fails
     */
    public boolean assignToken(
            long appointmentId,
            long tokenId
    ) throws SQLException {

        if (appointmentId <= 0) {
            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }

        if (tokenId <= 0) {
            throw new IllegalArgumentException(
                    "Token ID must be greater than zero."
            );
        }

        String sql = """
                UPDATE appointments
                SET token_id = ?,
                    status = 'CHECKED_IN'
                WHERE appointment_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, tokenId);
            statement.setLong(2, appointmentId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Cancels an appointment.
     *
     * @param appointmentId appointment ID
     * @return true if cancelled successfully
     * @throws SQLException if database operation fails
     */
    public boolean cancel(long appointmentId)
            throws SQLException {

        return updateStatus(
                appointmentId,
                Status.CANCELLED
        );
    }

    /**
     * Converts a database row into an Appointment object.
     *
     * @param resultSet database result
     * @return populated Appointment object
     * @throws SQLException if database values cannot be read
     */
    private Appointment mapAppointment(
            ResultSet resultSet
    ) throws SQLException {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(
                resultSet.getLong("appointment_id")
        );

        appointment.setCustomerId(
                resultSet.getInt("customer_id")
        );

        appointment.setServiceId(
                resultSet.getInt("service_id")
        );

        Date appointmentDate =
                resultSet.getDate("appointment_date");

        if (appointmentDate != null) {

            appointment.setAppointmentDate(
                    appointmentDate.toLocalDate()
            );
        }

        Time appointmentTime =
                resultSet.getTime("appointment_time");

        if (appointmentTime != null) {

            appointment.setAppointmentTime(
                    appointmentTime.toLocalTime()
            );
        }

        String statusValue =
                resultSet.getString("status");

        if (statusValue != null
                && !statusValue.isBlank()) {

            try {

                appointment.setStatus(
                        Status.valueOf(
                                statusValue
                                        .trim()
                                        .toUpperCase()
                        )
                );

            } catch (IllegalArgumentException e) {

                throw new SQLException(
                        "Invalid appointment status in database: "
                                + statusValue,
                        e
                );
            }
        }

        long tokenId =
                resultSet.getLong("token_id");

        if (!resultSet.wasNull()) {

            appointment.setTokenId(tokenId);
        }

        return appointment;
    }
}
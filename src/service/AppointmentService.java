package service;

import dao.AppointmentDAO;
import model.Appointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Service layer for appointment-related business operations.
 *
 * The service layer sits between the UI and DAO layer.
 * It validates business rules before accessing the database.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    /**
     * Creates an AppointmentService with the default DAO.
     */
    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Creates an AppointmentService with a custom DAO.
     * Useful for testing and dependency injection.
     *
     * @param appointmentDAO appointment DAO
     */
    public AppointmentService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = Objects.requireNonNull(
                appointmentDAO,
                "AppointmentDAO cannot be null."
        );
    }

    /**
     * Creates a new appointment after validating its data.
     *
     * @param appointment appointment to create
     * @return generated appointment ID
     * @throws SQLException if database operation fails
     */
    public long createAppointment(Appointment appointment)
            throws SQLException {

        validateAppointment(appointment);

        // New appointments always start as SCHEDULED.
        appointment.setStatus(Appointment.Status.SCHEDULED);

        return appointmentDAO.save(appointment);
    }

    /**
     * Finds an appointment by ID.
     *
     * @param appointmentId appointment ID
     * @return appointment if found, otherwise null
     * @throws SQLException if database operation fails
     */
    public Appointment getAppointment(long appointmentId)
            throws SQLException {

        validateId(appointmentId, "Appointment ID");

        return appointmentDAO.findById(appointmentId);
    }

    /**
     * Changes the status of an appointment.
     *
     * @param appointmentId appointment ID
     * @param status new status
     * @return true if successfully updated
     * @throws SQLException if database operation fails
     */
    public boolean updateStatus(
            long appointmentId,
            Appointment.Status status
    ) throws SQLException {

        validateId(appointmentId, "Appointment ID");

        if (status == null) {
            throw new IllegalArgumentException(
                    "Appointment status cannot be null."
            );
        }

        return appointmentDAO.updateStatus(
                appointmentId,
                status
        );
    }

    /**
     * Checks in a scheduled appointment.
     *
     * @param appointmentId appointment ID
     * @return true if successfully checked in
     * @throws SQLException if database operation fails
     */
    public boolean checkIn(long appointmentId)
            throws SQLException {

        validateId(appointmentId, "Appointment ID");

        Appointment appointment =
                appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            return false;
        }

        if (appointment.getStatus()
                != Appointment.Status.SCHEDULED) {

            throw new IllegalStateException(
                    "Only scheduled appointments can be checked in."
            );
        }

        return appointmentDAO.updateStatus(
                appointmentId,
                Appointment.Status.CHECKED_IN
        );
    }

    /**
     * Marks an appointment as completed.
     *
     * @param appointmentId appointment ID
     * @return true if successfully completed
     * @throws SQLException if database operation fails
     */
    public boolean complete(long appointmentId)
            throws SQLException {

        validateId(appointmentId, "Appointment ID");

        Appointment appointment =
                appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            return false;
        }

        if (appointment.getStatus()
                == Appointment.Status.CANCELLED) {

            throw new IllegalStateException(
                    "A cancelled appointment cannot be completed."
            );
        }

        if (appointment.getStatus()
                == Appointment.Status.MISSED) {

            throw new IllegalStateException(
                    "A missed appointment cannot be completed."
            );
        }

        return appointmentDAO.updateStatus(
                appointmentId,
                Appointment.Status.COMPLETED
        );
    }

    /**
     * Cancels an appointment.
     *
     * @param appointmentId appointment ID
     * @return true if successfully cancelled
     * @throws SQLException if database operation fails
     */
    public boolean cancel(long appointmentId)
            throws SQLException {

        validateId(appointmentId, "Appointment ID");

        Appointment appointment =
                appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            return false;
        }

        if (appointment.getStatus()
                == Appointment.Status.COMPLETED) {

            throw new IllegalStateException(
                    "A completed appointment cannot be cancelled."
            );
        }

        if (appointment.getStatus()
                == Appointment.Status.CANCELLED) {

            return false;
        }

        return appointmentDAO.cancel(appointmentId);
    }

    /**
     * Assigns a generated token to an appointment.
     *
     * @param appointmentId appointment ID
     * @param tokenId token ID
     * @return true if successfully assigned
     * @throws SQLException if database operation fails
     */
    public boolean assignToken(
            long appointmentId,
            long tokenId
    ) throws SQLException {

        validateId(appointmentId, "Appointment ID");
        validateId(tokenId, "Token ID");

        Appointment appointment =
                appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            return false;
        }

        if (appointment.getStatus()
                == Appointment.Status.CANCELLED) {

            throw new IllegalStateException(
                    "Cannot assign a token to a cancelled appointment."
            );
        }

        if (appointment.getStatus()
                == Appointment.Status.COMPLETED) {

            throw new IllegalStateException(
                    "Cannot assign a token to a completed appointment."
            );
        }

        return appointmentDAO.assignToken(
                appointmentId,
                tokenId
        );
    }

    /**
     * Marks an appointment as missed.
     *
     * @param appointmentId appointment ID
     * @return true if successfully marked as missed
     * @throws SQLException if database operation fails
     */
    public boolean markMissed(long appointmentId)
            throws SQLException {

        validateId(appointmentId, "Appointment ID");

        Appointment appointment =
                appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            return false;
        }

        if (appointment.getStatus()
                == Appointment.Status.COMPLETED) {

            throw new IllegalStateException(
                    "A completed appointment cannot be marked as missed."
            );
        }

        if (appointment.getStatus()
                == Appointment.Status.CANCELLED) {

            throw new IllegalStateException(
                    "A cancelled appointment cannot be marked as missed."
            );
        }

        return appointmentDAO.updateStatus(
                appointmentId,
                Appointment.Status.MISSED
        );
    }

    /**
     * Validates appointment information before database insertion.
     */
    private void validateAppointment(
            Appointment appointment) {

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment cannot be null."
            );
        }

        if (appointment.getCustomerId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid customer ID."
            );
        }

        if (appointment.getServiceId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid service ID."
            );
        }

        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be null."
            );
        }

        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException(
                    "Appointment time cannot be null."
            );
        }

        /*
         * New appointments cannot be created for a past date.
         */
        if (appointment.getAppointmentDate()
                .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }
    }

    /**
     * Validates positive database IDs.
     */
    private void validateId(
            long id,
            String fieldName) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
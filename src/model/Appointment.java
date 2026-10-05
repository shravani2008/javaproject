package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a scheduled customer appointment.
 */
public class Appointment {

    public enum Status {
        SCHEDULED,
        CHECKED_IN,
        COMPLETED,
        CANCELLED,
        MISSED
    }

    private long appointmentId;
    private int customerId;
    private int serviceId;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private Status status;
    private Long tokenId;

    public Appointment() {
        this.status = Status.SCHEDULED;
    }

    public Appointment(int customerId,
                       int serviceId,
                       LocalDate appointmentDate,
                       LocalTime appointmentTime) {

        setCustomerId(customerId);
        setServiceId(serviceId);
        setAppointmentDate(appointmentDate);
        setAppointmentTime(appointmentTime);

        this.status = Status.SCHEDULED;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        if (appointmentId < 0) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be negative.");
        }
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID.");
        }
        this.customerId = customerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        if (serviceId <= 0) {
            throw new IllegalArgumentException("Invalid service ID.");
        }
        this.serviceId = serviceId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be null.");
        }
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        if (appointmentTime == null) {
            throw new IllegalArgumentException(
                    "Appointment time cannot be null.");
        }
        this.appointmentTime = appointmentTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Appointment status cannot be null.");
        }
        this.status = status;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        if (tokenId != null && tokenId <= 0) {
            throw new IllegalArgumentException("Invalid token ID.");
        }
        this.tokenId = tokenId;
    }

    public boolean isScheduled() {
        return status == Status.SCHEDULED;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                ", tokenId=" + tokenId +
                '}';
    }
}
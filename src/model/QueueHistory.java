package model;

import java.time.LocalDateTime;

/**
 * Represents an event recorded in the queue history.
 *
 * Every important token state transition can be recorded here,
 * such as WAITING -> CALLED or SERVING -> COMPLETED.
 */
public class QueueHistory {

    private long historyId;

    private long tokenId;
    private int customerId;
    private int serviceId;
    private Integer counterId;

    private String oldStatus;
    private String newStatus;

    private LocalDateTime eventTime;
    private String notes;

    public QueueHistory() {
        this.eventTime = LocalDateTime.now();
    }

    public QueueHistory(long tokenId,
                        int customerId,
                        int serviceId,
                        Integer counterId,
                        String oldStatus,
                        String newStatus,
                        String notes) {

        setTokenId(tokenId);
        setCustomerId(customerId);
        setServiceId(serviceId);
        setCounterId(counterId);
        setOldStatus(oldStatus);
        setNewStatus(newStatus);

        this.eventTime = LocalDateTime.now();

        setNotes(notes);
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        if (historyId < 0) {
            throw new IllegalArgumentException(
                    "History ID cannot be negative."
            );
        }

        this.historyId = historyId;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        if (tokenId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid token ID."
            );
        }

        this.tokenId = tokenId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid customer ID."
            );
        }

        this.customerId = customerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        if (serviceId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid service ID."
            );
        }

        this.serviceId = serviceId;
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        if (counterId != null && counterId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid counter ID."
            );
        }

        this.counterId = counterId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus == null
                ? null
                : oldStatus.trim();
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "New status cannot be empty."
            );
        }

        this.newStatus = newStatus.trim();
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        if (eventTime == null) {
            throw new IllegalArgumentException(
                    "Event time cannot be null."
            );
        }

        this.eventTime = eventTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null
                ? null
                : notes.trim();
    }

    @Override
    public String toString() {
        return "QueueHistory{" +
                "historyId=" + historyId +
                ", tokenId=" + tokenId +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", counterId=" + counterId +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", eventTime=" + eventTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
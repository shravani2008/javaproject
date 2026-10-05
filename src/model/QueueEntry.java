package model;

import java.time.LocalDateTime;

/**
 * Represents a customer's position in the active queue.
 */
public class QueueEntry {

    /**
     * Status of a queue entry.
     *
     * This matches queue_entries.status in MySQL.
     */
    public enum Status {
        WAITING,
        CALLED,
        SERVING,
        COMPLETED,
        CANCELLED,
        SKIPPED
    }

    private long queueEntryId;
    private long tokenId;

    private int queuePosition;
    private double effectivePriority;

    private LocalDateTime joinedAt;
    private LocalDateTime lastPriorityUpdate;

    private Status status;

    public QueueEntry() {
        this.joinedAt = LocalDateTime.now();
        this.status = Status.WAITING;
    }

    public QueueEntry(
            long tokenId,
            int queuePosition,
            double effectivePriority) {

        setTokenId(tokenId);
        setQueuePosition(queuePosition);
        setEffectivePriority(effectivePriority);

        this.joinedAt = LocalDateTime.now();
        this.status = Status.WAITING;
    }

    public long getQueueEntryId() {
        return queueEntryId;
    }

    public void setQueueEntryId(long queueEntryId) {
        if (queueEntryId < 0) {
            throw new IllegalArgumentException(
                    "Queue entry ID cannot be negative."
            );
        }

        this.queueEntryId = queueEntryId;
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

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        if (queuePosition <= 0) {
            throw new IllegalArgumentException(
                    "Queue position must be greater than zero."
            );
        }

        this.queuePosition = queuePosition;
    }

    public double getEffectivePriority() {
        return effectivePriority;
    }

    public void setEffectivePriority(double effectivePriority) {
        if (effectivePriority < 0) {
            throw new IllegalArgumentException(
                    "Priority cannot be negative."
            );
        }

        this.effectivePriority = effectivePriority;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastPriorityUpdate() {
        return lastPriorityUpdate;
    }

    public void setLastPriorityUpdate(
            LocalDateTime lastPriorityUpdate) {

        this.lastPriorityUpdate = lastPriorityUpdate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Queue status cannot be null."
            );
        }

        this.status = status;
    }

    @Override
    public String toString() {

        return "QueueEntry{" +
                "queueEntryId=" + queueEntryId +
                ", tokenId=" + tokenId +
                ", queuePosition=" + queuePosition +
                ", effectivePriority=" + effectivePriority +
                ", status=" + status +
                '}';
    }
}
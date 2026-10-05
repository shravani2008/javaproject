package model;

import java.time.LocalDateTime;

/**
 * Represents a customer token in the queue.
 */
public class Token {

    public enum Status {
        GENERATED,
        WAITING,
        CALLED,
        SERVING,
        COMPLETED,
        CANCELLED,
        SKIPPED
    }

    public enum PriorityLevel {
        NORMAL,
        PRIORITY,
        APPOINTMENT
    }

    private long tokenId;
    private String tokenCode;
    private int customerId;
    private int serviceId;
    private Integer counterId;

    private Status status;
    private PriorityLevel priorityLevel;

    private LocalDateTime arrivalTime;
    private LocalDateTime calledTime;
    private LocalDateTime serviceStartTime;
    private LocalDateTime completionTime;

    private Double estimatedWaitMinutes;
    private Double actualWaitMinutes;
    private Double actualServiceMinutes;

    public Token() {
        this.status = Status.GENERATED;
        this.priorityLevel = PriorityLevel.NORMAL;
        this.arrivalTime = LocalDateTime.now();
    }

    public Token(String tokenCode,
                 int customerId,
                 int serviceId,
                 PriorityLevel priorityLevel) {

        setTokenCode(tokenCode);
        setCustomerId(customerId);
        setServiceId(serviceId);

        this.priorityLevel = priorityLevel == null
                ? PriorityLevel.NORMAL
                : priorityLevel;

        this.status = Status.GENERATED;
        this.arrivalTime = LocalDateTime.now();
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        if (tokenId < 0) {
            throw new IllegalArgumentException("Token ID cannot be negative.");
        }
        this.tokenId = tokenId;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        if (tokenCode == null || tokenCode.isBlank()) {
            throw new IllegalArgumentException("Token code cannot be empty.");
        }
        this.tokenCode = tokenCode.trim().toUpperCase();
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

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        if (counterId != null && counterId <= 0) {
            throw new IllegalArgumentException("Invalid counter ID.");
        }
        this.counterId = counterId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Token status cannot be null.");
        }
        this.status = status;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        if (priorityLevel == null) {
            throw new IllegalArgumentException(
                    "Priority level cannot be null.");
        }
        this.priorityLevel = priorityLevel;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(LocalDateTime calledTime) {
        this.calledTime = calledTime;
    }

    public LocalDateTime getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(LocalDateTime serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public Double getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(Double estimatedWaitMinutes) {
        validateMinutes(estimatedWaitMinutes);
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public Double getActualWaitMinutes() {
        return actualWaitMinutes;
    }

    public void setActualWaitMinutes(Double actualWaitMinutes) {
        validateMinutes(actualWaitMinutes);
        this.actualWaitMinutes = actualWaitMinutes;
    }

    public Double getActualServiceMinutes() {
        return actualServiceMinutes;
    }

    public void setActualServiceMinutes(Double actualServiceMinutes) {
        validateMinutes(actualServiceMinutes);
        this.actualServiceMinutes = actualServiceMinutes;
    }

    private void validateMinutes(Double value) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(
                    "Time value cannot be negative.");
        }
    }

    public boolean isWaiting() {
        return status == Status.WAITING;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId=" + tokenId +
                ", tokenCode='" + tokenCode + '\'' +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", counterId=" + counterId +
                ", status=" + status +
                ", priorityLevel=" + priorityLevel +
                '}';
    }
}
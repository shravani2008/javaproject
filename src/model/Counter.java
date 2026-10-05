package model;

/**
 * Represents a service counter in the Smart Queue Management System.
 *
 * A counter may be assigned to a particular service and/or staff member.
 */
public class Counter {

    public enum Status {
        IDLE,
        BUSY,
        OFFLINE
    }

    private int counterId;
    private String counterNumber;
    private String counterName;
    private Status status;
    private Integer supportedServiceId;
    private Integer staffUserId;
    private int totalServed;

    public Counter() {
        this.status = Status.IDLE;
        this.totalServed = 0;
    }

    public Counter(String counterNumber,
                   String counterName,
                   Status status) {

        setCounterNumber(counterNumber);
        setCounterName(counterName);

        this.status = status == null
                ? Status.IDLE
                : status;

        this.totalServed = 0;
    }

    public Counter(int counterId,
                   String counterNumber,
                   String counterName,
                   Status status,
                   Integer supportedServiceId,
                   Integer staffUserId,
                   int totalServed) {

        this(counterNumber, counterName, status);

        setCounterId(counterId);
        setSupportedServiceId(supportedServiceId);
        setStaffUserId(staffUserId);
        setTotalServed(totalServed);
    }

    public int getCounterId() {
        return counterId;
    }

    public void setCounterId(int counterId) {
        if (counterId < 0) {
            throw new IllegalArgumentException(
                    "Counter ID cannot be negative."
            );
        }

        this.counterId = counterId;
    }

    public String getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(String counterNumber) {
        if (counterNumber == null || counterNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Counter number cannot be empty."
            );
        }

        this.counterNumber = counterNumber.trim();
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        if (counterName == null || counterName.isBlank()) {
            throw new IllegalArgumentException(
                    "Counter name cannot be empty."
            );
        }

        this.counterName = counterName.trim();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Counter status cannot be null."
            );
        }

        this.status = status;
    }

    public Integer getSupportedServiceId() {
        return supportedServiceId;
    }

    public void setSupportedServiceId(Integer supportedServiceId) {
        if (supportedServiceId != null && supportedServiceId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid service ID."
            );
        }

        this.supportedServiceId = supportedServiceId;
    }

    public Integer getStaffUserId() {
        return staffUserId;
    }

    public void setStaffUserId(Integer staffUserId) {
        if (staffUserId != null && staffUserId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid staff user ID."
            );
        }

        this.staffUserId = staffUserId;
    }

    public int getTotalServed() {
        return totalServed;
    }

    public void setTotalServed(int totalServed) {
        if (totalServed < 0) {
            throw new IllegalArgumentException(
                    "Total served cannot be negative."
            );
        }

        this.totalServed = totalServed;
    }

    /**
     * Marks the counter as available for serving.
     */
    public void makeAvailable() {
        this.status = Status.IDLE;
    }

    /**
     * Marks the counter as currently serving a customer.
     */
    public void startServing() {
        this.status = Status.BUSY;
    }

    /**
     * Marks the counter as unavailable.
     */
    public void takeOffline() {
        this.status = Status.OFFLINE;
    }

    /**
     * Increases the number of customers served by this counter.
     */
    public void incrementServedCount() {
        this.totalServed++;
    }

    public boolean isAvailable() {
        return status == Status.IDLE;
    }

    public boolean isBusy() {
        return status == Status.BUSY;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "counterId=" + counterId +
                ", counterNumber='" + counterNumber + '\'' +
                ", counterName='" + counterName + '\'' +
                ", status=" + status +
                ", supportedServiceId=" + supportedServiceId +
                ", staffUserId=" + staffUserId +
                ", totalServed=" + totalServed +
                '}';
    }
}
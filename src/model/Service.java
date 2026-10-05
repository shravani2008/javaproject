package model;

/**
 * Represents a service offered by the organization.
 */
public class Service {

    private int serviceId;
    private String serviceCode;
    private String serviceName;
    private String description;
    private double averageServiceTime;
    private boolean priorityAllowed;
    private boolean active;

    public Service() {
        this.averageServiceTime = 5.0;
        this.priorityAllowed = true;
        this.active = true;
    }

    public Service(String serviceCode,
                   String serviceName,
                   String description,
                   double averageServiceTime,
                   boolean priorityAllowed) {

        setServiceCode(serviceCode);
        setServiceName(serviceName);
        setDescription(description);
        setAverageServiceTime(averageServiceTime);

        this.priorityAllowed = priorityAllowed;
        this.active = true;
    }

    public Service(int serviceId,
                   String serviceCode,
                   String serviceName,
                   String description,
                   double averageServiceTime,
                   boolean priorityAllowed,
                   boolean active) {

        this(serviceCode, serviceName, description,
                averageServiceTime, priorityAllowed);

        setServiceId(serviceId);
        this.active = active;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        if (serviceId < 0) {
            throw new IllegalArgumentException("Service ID cannot be negative.");
        }
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        if (serviceCode == null || serviceCode.isBlank()) {
            throw new IllegalArgumentException("Service code cannot be empty.");
        }
        this.serviceCode = serviceCode.trim().toUpperCase();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name cannot be empty.");
        }
        this.serviceName = serviceName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        if (averageServiceTime <= 0) {
            throw new IllegalArgumentException(
                    "Average service time must be greater than zero.");
        }
        this.averageServiceTime = averageServiceTime;
    }

    public boolean isPriorityAllowed() {
        return priorityAllowed;
    }

    public void setPriorityAllowed(boolean priorityAllowed) {
        this.priorityAllowed = priorityAllowed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", serviceCode='" + serviceCode + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", averageServiceTime=" + averageServiceTime +
                ", priorityAllowed=" + priorityAllowed +
                ", active=" + active +
                '}';
    }
}
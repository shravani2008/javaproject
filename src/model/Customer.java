package model;

import java.time.LocalDate;

/**
 * Represents a customer using the queue management system.
 */
public class Customer extends User {

    public enum CustomerType {
        NORMAL,
        PRIORITY
    }

    private int customerId;
    private CustomerType customerType;
    private LocalDate dateOfBirth;

    public Customer() {
        super();
        setRole(Role.CUSTOMER);
        this.customerType = CustomerType.NORMAL;
    }

    public Customer(String username, String passwordHash, String fullName,
                    String email, String phone,
                    CustomerType customerType,
                    LocalDate dateOfBirth) {

        super(username, passwordHash, fullName, email, phone, Role.CUSTOMER);

        setCustomerType(customerType);
        this.dateOfBirth = dateOfBirth;
    }

    public Customer(int userId, int customerId,
                    String username, String passwordHash,
                    String fullName, String email, String phone,
                    CustomerType customerType,
                    LocalDate dateOfBirth,
                    boolean active) {

        super(userId, username, passwordHash, fullName,
                email, phone, Role.CUSTOMER, active);

        setCustomerId(customerId);
        setCustomerType(customerType);
        this.dateOfBirth = dateOfBirth;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId < 0) {
            throw new IllegalArgumentException("Customer ID cannot be negative.");
        }
        this.customerId = customerId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        if (customerType == null) {
            throw new IllegalArgumentException("Customer type cannot be null.");
        }
        this.customerType = customerType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isPriorityCustomer() {
        return customerType == CustomerType.PRIORITY;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", userId=" + getUserId() +
                ", fullName='" + getFullName() + '\'' +
                ", customerType=" + customerType +
                '}';
    }
}
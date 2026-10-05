package model;

import java.util.Objects;

/**
 * Represents a system user.
 *
 * A User can be a CUSTOMER, STAFF, or ADMIN.
 */
public class User {

    public enum Role {
        CUSTOMER,
        STAFF,
        ADMIN
    }

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean active;

    public User() {
        this.active = true;
    }

    public User(String username, String passwordHash, String fullName,
                String email, String phone, Role role) {

        setUsername(username);
        setPasswordHash(passwordHash);
        setFullName(fullName);
        setEmail(email);
        setPhone(phone);
        setRole(role);
        this.active = true;
    }

    public User(int userId, String username, String passwordHash,
                String fullName, String email, String phone,
                Role role, boolean active) {

        this(username, passwordHash, fullName, email, phone, role);
        setUserId(userId);
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative.");
        }
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "Role cannot be null.");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
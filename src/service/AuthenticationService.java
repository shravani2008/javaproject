package service;

import dao.UserDAO;
import model.User;

/**
 * Handles authentication and the currently logged-in user.
 *
 * Responsibilities:
 * 1. Validate login input.
 * 2. Authenticate against MySQL through UserDAO.
 * 3. Keep track of the current session user.
 * 4. Provide role-based access information.
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    private User currentUser;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticate a user.
     *
     * NOTE:
     * The current database stores password_hash.
     * Therefore the password supplied here must match
     * the value stored in password_hash.
     */
    public User login(String username, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty.");
        }

        String cleanUsername = username.trim();

        User user = userDAO.authenticate(
                cleanUsername,
                password
        );

        if (user == null) {
            return null;
        }

        currentUser = user;

        return currentUser;
    }

    /**
     * Logout the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks whether the current user is a customer.
     */
    public boolean isCustomer() {
        return isLoggedIn()
                && currentUser.getRole() == User.Role.CUSTOMER;
    }

    /**
     * Checks whether the current user is staff.
     */
    public boolean isStaff() {
        return isLoggedIn()
                && currentUser.getRole() == User.Role.STAFF;
    }

    /**
     * Checks whether the current user is admin.
     */
    public boolean isAdmin() {
        return isLoggedIn()
                && currentUser.getRole() == User.Role.ADMIN;
    }

    /**
     * Returns the current user's ID.
     */
    public int getCurrentUserId() {

        if (!isLoggedIn()) {
            throw new IllegalStateException(
                    "No user is currently logged in.");
        }

        return currentUser.getUserId();
    }

    /**
     * Returns the current user's role.
     */
    public User.Role getCurrentRole() {

        if (!isLoggedIn()) {
            throw new IllegalStateException(
                    "No user is currently logged in.");
        }

        return currentUser.getRole();
    }
}
package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class CustomerRegistrationView {

    private final JFrame frame;
    private final service.AuthenticationService authenticationService;

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public CustomerRegistrationView(
            JFrame frame,
            service.AuthenticationService authenticationService) {

        this.frame = frame;
        this.authenticationService = authenticationService;
    }

    public void show() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel(
                "CREATE CUSTOMER ACCOUNT",
                SwingConstants.CENTER);

        heading.setFont(
                new Font("Arial", Font.BOLD, 25));

        heading.setBorder(
                new EmptyBorder(25, 10, 20, 10));

        JPanel formPanel = new JPanel(
                new GridLayout(6, 2, 15, 15));

        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
                new EmptyBorder(35, 70, 25, 70));

        fullNameField = new JTextField();
        usernameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(fullNameField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);

        JButton registerButton =
                new JButton("CREATE ACCOUNT");

        JButton backButton =
                new JButton("BACK TO LOGIN");

        registerButton.setBackground(
                new Color(37, 99, 235));
        registerButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        20,
                        15));

        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        JPanel centerPanel = new JPanel(
                new BorderLayout());

        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        registerButton.addActionListener(
                e -> registerCustomer());

        backButton.addActionListener(
                e -> goBack());

        frame.setTitle(
                "SmartQueue | Customer Registration");

        frame.setContentPane(mainPanel);
        frame.setSize(700, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void registerCustomer() {

        String fullName =
                fullNameField.getText().trim();

        String username =
                usernameField.getText().trim();

        String email =
                emailField.getText().trim();

        String phone =
                phoneField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        String confirmPassword =
                new String(
                        confirmPasswordField.getPassword());

        if (fullName.isEmpty()) {
            showError("Full name is required.");
            return;
        }

        if (username.length() < 3) {
            showError(
                    "Username must contain at least 3 characters.");
            return;
        }

        if (password.length() < 6) {
            showError(
                    "Password must contain at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        Connection connection = null;

        try {

            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            String checkSql =
                    "SELECT user_id FROM users " +
                    "WHERE username = ? LIMIT 1";

            try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                check.setString(1, username);

                try (ResultSet rs =
                             check.executeQuery()) {

                    if (rs.next()) {

                        connection.rollback();

                        showError(
                                "Username already exists.");

                        return;
                    }
                }
            }

            String userSql =
                    "INSERT INTO users " +
                    "(username, password_hash, full_name, " +
                    "email, phone, role, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            int userId;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 userSql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, fullName);
                statement.setString(4, email);
                statement.setString(5, phone);
                statement.setString(6, "CUSTOMER");
                statement.setBoolean(7, true);

                statement.executeUpdate();

                try (ResultSet keys =
                             statement.getGeneratedKeys()) {

                    if (!keys.next()) {
                        throw new SQLException(
                                "Unable to create user.");
                    }

                    userId = keys.getInt(1);
                }
            }

            String customerSql =
                    "INSERT INTO customers " +
                    "(user_id, customer_type) " +
                    "VALUES (?, ?)";

            try (PreparedStatement statement =
                         connection.prepareStatement(customerSql)) {

                statement.setInt(1, userId);
                statement.setString(2, "NORMAL");

                statement.executeUpdate();
            }

            connection.commit();

            JOptionPane.showMessageDialog(
                    frame,
                    "Customer account created successfully!\n"
                            + "You can now login.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            goBack();

        } catch (SQLException e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
            }

            JOptionPane.showMessageDialog(
                    frame,
                    "Registration failed:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

        } finally {

            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    private void goBack() {

        new LoginView(
                frame,
                authenticationService
        ).show();
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                frame,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
    }
}
package ui;

import model.User;
import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView {

    private final JFrame frame;
    private final AuthenticationService authenticationService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginView(
            JFrame frame,
            AuthenticationService authenticationService) {

        this.frame = frame;
        this.authenticationService = authenticationService;
    }

    public void show() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(400, 600));
        leftPanel.setBackground(new Color(37, 99, 235));

        leftPanel.setLayout(
                new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.setBorder(
                new EmptyBorder(150, 45, 40, 45));

        JLabel logo = new JLabel("SMARTQUEUE");

        logo.setForeground(Color.WHITE);
        logo.setFont(
                new Font("Arial", Font.BOLD, 32));

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(
                "Smart Queue Management System");

        title.setForeground(Color.WHITE);
        title.setFont(
                new Font("Arial", Font.BOLD, 17));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
                "<html><center>"
                        + "Manage queues efficiently<br>"
                        + "Reduce waiting time<br>"
                        + "Improve customer service"
                        + "</center></html>");

        description.setForeground(
                new Color(220, 230, 255));

        description.setFont(
                new Font("Arial", Font.PLAIN, 14));

        description.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(description);

        // RIGHT PANEL
        JPanel rightPanel = new JPanel();

        rightPanel.setLayout(
                new BoxLayout(
                        rightPanel,
                        BoxLayout.Y_AXIS));

        rightPanel.setBorder(
                new EmptyBorder(90, 70, 60, 70));

        rightPanel.setBackground(Color.WHITE);

        JLabel heading =
                new JLabel("Welcome Back");

        heading.setFont(
                new Font("Arial", Font.BOLD, 28));

        heading.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        JLabel subHeading =
                new JLabel("Login to SmartQueue");

        subHeading.setFont(
                new Font("Arial", Font.PLAIN, 14));

        subHeading.setForeground(Color.GRAY);

        subHeading.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        usernameField =
                new JTextField();

        usernameField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42));

        usernameField.setBorder(
                BorderFactory.createTitledBorder(
                        "Username"));

        passwordField =
                new JPasswordField();

        passwordField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42));

        passwordField.setBorder(
                BorderFactory.createTitledBorder(
                        "Password"));

        JButton loginButton =
                new JButton("LOGIN");

        loginButton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45));

        loginButton.setBackground(
                new Color(37, 99, 235));

        loginButton.setForeground(Color.WHITE);

        loginButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        JButton registerButton =
                new JButton(
                        "CREATE CUSTOMER ACCOUNT");

        registerButton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42));

        messageLabel =
                new JLabel(" ");

        messageLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        messageLabel.setForeground(Color.RED);

        loginButton.addActionListener(
                e -> login());

        registerButton.addActionListener(e -> {

            new CustomerRegistrationView(
                    frame,
                    authenticationService
            ).show();

        });

        rightPanel.add(heading);
        rightPanel.add(
                Box.createVerticalStrut(10));

        rightPanel.add(subHeading);
        rightPanel.add(
                Box.createVerticalStrut(35));

        rightPanel.add(usernameField);
        rightPanel.add(
                Box.createVerticalStrut(15));

        rightPanel.add(passwordField);
        rightPanel.add(
                Box.createVerticalStrut(20));

        rightPanel.add(loginButton);
        rightPanel.add(
                Box.createVerticalStrut(12));

        rightPanel.add(messageLabel);
        rightPanel.add(
                Box.createVerticalStrut(15));

        rightPanel.add(registerButton);

        mainPanel.add(
                leftPanel,
                BorderLayout.WEST);

        mainPanel.add(
                rightPanel,
                BorderLayout.CENTER);

        frame.setTitle(
                "SmartQueue | Login");

        frame.setContentPane(mainPanel);

        frame.setSize(1000, 600);

        frame.setResizable(false);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword());

        if (username.isEmpty()) {

            messageLabel.setText(
                    "Please enter username.");

            return;
        }

        if (password.isEmpty()) {

            messageLabel.setText(
                    "Please enter password.");

            return;
        }

        try {

            User user =
                    authenticationService.login(
                            username,
                            password);

            if (user == null) {

                messageLabel.setText(
                        "Invalid username or password.");

                return;
            }

            openDashboard(user);

        } catch (RuntimeException e) {

            messageLabel.setText(
                    "Unable to connect to database.");

            e.printStackTrace();
        }
    }

    private void openDashboard(User user) {

        switch (user.getRole()) {

            case CUSTOMER:

                new CustomerDashboard(
                        frame,
                        user,
                        authenticationService
                ).show();

                break;

            case STAFF:

                new StaffDashboard(
                        frame,
                        user.getUserId(),
                        user.getFullName(),
                        authenticationService
                ).show();

                break;

            case ADMIN:

                new AdminDashboard(
                        frame,
                        user.getUserId(),
                        user.getFullName(),
                        authenticationService
                ).show();

                break;

            default:

                messageLabel.setText(
                        "Unsupported user role.");
        }
    }
}
package ui;

import dao.TokenDAO;
import model.Token;
import model.User;
import service.AuthenticationService;
import service.QueueService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDashboard {

    private final JFrame frame;
    private final User user;
    private final AuthenticationService authenticationService;
    private final QueueService queueService;
    private final TokenDAO tokenDAO;

    private long activeTokenId = -1;

    private JLabel tokenLabel;
    private JLabel positionLabel;
    private JLabel waitingLabel;
    private JLabel servingLabel;
    private JLabel estimatedLabel;
    private JLabel statusLabel;

    private JButton joinButton;
    private JButton cancelButton;

    private Timer refreshTimer;

    public CustomerDashboard(
            JFrame frame,
            User user,
            AuthenticationService authenticationService) {

        this.frame = frame;
        this.user = user;
        this.authenticationService = authenticationService;
        this.queueService = new QueueService();
        this.tokenDAO = new TokenDAO();
    }

    public void show() {

        frame.setTitle("SmartQueue - Customer Dashboard");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ---------------- HEADER ----------------

        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("SMARTQUEUE");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel welcome = new JLabel(
                "Welcome, " + user.getFullName());

        welcome.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        header.add(title, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.add(welcome);
        rightHeader.add(logoutButton);

        header.add(rightHeader, BorderLayout.EAST);

        // ---------------- CENTER ----------------

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));

        JPanel tokenPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        JPanel tokenCard = createCard("MY TOKEN");

        tokenLabel = new JLabel("Not joined");
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 30));
        tokenLabel.setHorizontalAlignment(SwingConstants.CENTER);

        tokenCard.add(tokenLabel, BorderLayout.CENTER);

        JPanel positionCard = createCard("QUEUE POSITION");

        positionLabel = new JLabel("-");
        positionLabel.setFont(new Font("Arial", Font.BOLD, 30));
        positionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        positionCard.add(positionLabel, BorderLayout.CENTER);

        tokenPanel.add(tokenCard);
        tokenPanel.add(positionCard);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        waitingLabel = new JLabel("People Waiting: -");
        servingLabel = new JLabel("Currently Serving: -");
        estimatedLabel = new JLabel("Estimated Wait: -");

        waitingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        servingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        estimatedLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        infoPanel.add(waitingLabel);
        infoPanel.add(servingLabel);
        infoPanel.add(estimatedLabel);

        centerPanel.add(tokenPanel, BorderLayout.NORTH);
        centerPanel.add(infoPanel, BorderLayout.CENTER);

        // ---------------- BUTTONS ----------------

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        statusLabel = new JLabel("You have not joined the queue.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        joinButton = new JButton("JOIN QUEUE");
        cancelButton = new JButton("CANCEL QUEUE");

        joinButton.setFont(new Font("Arial", Font.BOLD, 15));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 15));

        joinButton.addActionListener(e -> joinQueue());
        cancelButton.addActionListener(e -> cancelQueue());

        buttonPanel.add(joinButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        loadCustomerToken();
        refreshDashboard();

        refreshTimer = new Timer(3000, e -> refreshDashboard());
        refreshTimer.start();
    }

    private JPanel createCard(String titleText) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titleText));

        return panel;
    }

    // =========================================================
    // JOIN QUEUE
    // =========================================================

    private void joinQueue() {

        try {

            if (activeTokenId > 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        "You already have an active token.",
                        "Queue",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            int customerId = getCustomerId();

            if (customerId <= 0) {
                throw new IllegalStateException(
                        "Customer account was not found."
                );
            }

            String tokenCode = queueService.generateNextTokenCode();

            Token token = new Token(
                    tokenCode,
                    customerId,
                    1,
                    Token.PriorityLevel.NORMAL
            );

            long tokenId = tokenDAO.createToken(token);

            if (tokenId <= 0) {
                throw new IllegalStateException(
                        "Unable to create token."
                );
            }

            token.setTokenId(tokenId);

            queueService.joinQueue(token);

            activeTokenId = tokenId;

            JOptionPane.showMessageDialog(
                    frame,
                    "Queue joined successfully!\n\nYour Token: "
                            + tokenCode,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshDashboard();

        } catch (Exception e) {

            showError(e);
        }
    }

    // =========================================================
    // CANCEL QUEUE
    // =========================================================

    private void cancelQueue() {

        if (activeTokenId <= 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "You do not have an active queue token.",
                    "Queue",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to cancel your queue?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean success =
                    queueService.cancelQueue(activeTokenId);

            if (success) {

                activeTokenId = -1;

                JOptionPane.showMessageDialog(
                        frame,
                        "Your queue has been cancelled.",
                        "Queue",
                        JOptionPane.INFORMATION_MESSAGE
                );

                refreshDashboard();

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to cancel the queue.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            showError(e);
        }
    }

    // =========================================================
    // FIND CUSTOMER ID
    // =========================================================

    private int getCustomerId() throws Exception {

        String sql =
                "SELECT customer_id " +
                "FROM customers " +
                "WHERE user_id = ? " +
                "LIMIT 1";

        try (Connection connection =
                     util.DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, user.getUserId());

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("customer_id");
                }
            }
        }

        return -1;
    }

    // =========================================================
    // LOAD EXISTING TOKEN
    // =========================================================

    private void loadCustomerToken() {

        try {

            int customerId = getCustomerId();

            if (customerId <= 0) {
                return;
            }

            String sql =
                    "SELECT token_id " +
                    "FROM tokens " +
                    "WHERE customer_id = ? " +
                    "AND status IN ('WAITING','CALLED','SERVING') " +
                    "ORDER BY token_id DESC " +
                    "LIMIT 1";

            try (Connection connection =
                         util.DBConnection.getConnection();

                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, customerId);

                try (ResultSet resultSet =
                             statement.executeQuery()) {

                    if (resultSet.next()) {

                        activeTokenId =
                                resultSet.getLong("token_id");
                    }
                }
            }

        } catch (Exception e) {

            showError(e);
        }
    }

    // =========================================================
    // REFRESH DASHBOARD
    // =========================================================

    private void refreshDashboard() {

        try {

            int waiting =
                    queueService.getWaitingCount();

            waitingLabel.setText(
                    "People Waiting: " + waiting
            );

            updateServingToken();

            if (activeTokenId > 0) {

                Token token =
                        queueService.getToken(activeTokenId);

                if (token == null) {

                    activeTokenId = -1;
                    resetDisplay();
                    return;
                }

                tokenLabel.setText(
                        token.getTokenCode()
                );

                int position =
                        queueService.getCustomerPosition(
                                activeTokenId
                        );

                if (position > 0) {

                    positionLabel.setText(
                            String.valueOf(position)
                    );

                } else {

                    positionLabel.setText("-");
                }

                Double estimated =
                        token.getEstimatedWaitMinutes();

                if (estimated != null) {

                    estimatedLabel.setText(
                            "Estimated Wait: "
                                    + String.format(
                                    "%.0f minutes",
                                    estimated
                            )
                    );

                } else {

                    estimatedLabel.setText(
                            "Estimated Wait: -"
                    );
                }

                if (token.getStatus() != null) {

                    statusLabel.setText(
                            "Status: "
                                    + token.getStatus().name()
                    );

                }

                boolean active =
                        token.getStatus() == Token.Status.WAITING
                                || token.getStatus() == Token.Status.CALLED
                                || token.getStatus() == Token.Status.SERVING;

                if (!active) {

                    activeTokenId = -1;
                }

                joinButton.setEnabled(false);
                cancelButton.setEnabled(active);

            } else {

                resetDisplay();
            }

        } catch (Exception e) {

            showError(e);
        }
    }

    // =========================================================
    // CURRENTLY SERVING
    // =========================================================

    private void updateServingToken() {

        String sql =
                "SELECT token_code " +
                "FROM tokens " +
                "WHERE status = 'SERVING' " +
                "ORDER BY token_id DESC " +
                "LIMIT 1";

        try (Connection connection =
                     util.DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet =
                     statement.executeQuery()) {

            if (resultSet.next()) {

                servingLabel.setText(
                        "Currently Serving: "
                                + resultSet.getString(
                                "token_code"
                        )
                );

            } else {

                servingLabel.setText(
                        "Currently Serving: None"
                );
            }

        } catch (Exception e) {

            servingLabel.setText(
                    "Currently Serving: -"
            );
        }
    }

    // =========================================================
    // RESET DISPLAY
    // =========================================================

    private void resetDisplay() {

        tokenLabel.setText("Not joined");
        positionLabel.setText("-");
        estimatedLabel.setText("Estimated Wait: -");

        statusLabel.setText(
                "You have not joined the queue."
        );

        joinButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        if (refreshTimer != null) {
            refreshTimer.stop();
        }

        authenticationService.logout();

        new LoginView(
                frame,
                authenticationService
        ).show();
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
                frame,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
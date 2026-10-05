package ui;

import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard {

    private final JFrame frame;
    private final int userId;
    private final String name;
    private final AuthenticationService authenticationService;

    private JLabel customersLabel;
    private JLabel waitingLabel;
    private JLabel servingLabel;
    private JLabel completedLabel;

    public AdminDashboard(
            JFrame frame,
            int userId,
            String name,
            AuthenticationService authenticationService) {

        this.frame = frame;
        this.userId = userId;
        this.name = name;
        this.authenticationService = authenticationService;
    }

    public void show() {

        frame.setTitle("SmartQueue - Admin Dashboard");
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("SMARTQUEUE");
        title.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        JLabel welcome = new JLabel(
                "Welcome, Admin " + name
        );

        welcome.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        JPanel headerRight =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        JButton refreshButton =
                new JButton("Refresh");

        JButton logoutButton =
                new JButton("Logout");

        headerRight.add(welcome);
        headerRight.add(refreshButton);
        headerRight.add(logoutButton);

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                headerRight,
                BorderLayout.EAST
        );

        // =====================================================
        // STATISTICS
        // =====================================================

        JPanel statsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                15,
                                15
                        )
                );

        customersLabel =
                new JLabel(
                        "0",
                        SwingConstants.CENTER
                );

        waitingLabel =
                new JLabel(
                        "0",
                        SwingConstants.CENTER
                );

        servingLabel =
                new JLabel(
                        "0",
                        SwingConstants.CENTER
                );

        completedLabel =
                new JLabel(
                        "0",
                        SwingConstants.CENTER
                );

        statsPanel.add(
                createStatCard(
                        "CUSTOMERS",
                        customersLabel
                )
        );

        statsPanel.add(
                createStatCard(
                        "WAITING",
                        waitingLabel
                )
        );

        statsPanel.add(
                createStatCard(
                        "SERVING",
                        servingLabel
                )
        );

        statsPanel.add(
                createStatCard(
                        "COMPLETED",
                        completedLabel
                )
        );

        // =====================================================
        // MANAGEMENT BUTTONS
        // =====================================================

        JPanel managementPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                15,
                                15
                        )
                );

        JButton usersButton =
                new JButton("Manage Users");

        JButton countersButton =
                new JButton("Manage Counters");

        JButton servicesButton =
                new JButton("Manage Services");

        usersButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        countersButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        servicesButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        managementPanel.add(usersButton);
        managementPanel.add(countersButton);
        managementPanel.add(servicesButton);

        // =====================================================
        // ANALYTICS
        // =====================================================

        JTextArea analyticsArea =
                new JTextArea();

        analyticsArea.setEditable(false);

        analyticsArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        analyticsArea.setBorder(
                BorderFactory.createTitledBorder(
                        "Queue Analytics"
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        analyticsArea
                );

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );

        centerPanel.add(
                statsPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                managementPanel,
                BorderLayout.CENTER
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        // =====================================================
        // BUTTON EVENTS
        // =====================================================

        refreshButton.addActionListener(e -> {
            loadStatistics(analyticsArea);
        });

        usersButton.addActionListener(e -> {
            showUsers();
        });

        countersButton.addActionListener(e -> {
            showCounters();
        });

        servicesButton.addActionListener(e -> {
            showServices();
        });

        logoutButton.addActionListener(e -> {
            logout();
        });

        // Load statistics when dashboard opens
        loadStatistics(analyticsArea);
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private JPanel createStatCard(
            String title,
            JLabel valueLabel) {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        title
                )
        );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        panel.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // LOAD STATISTICS
    // =========================================================

    private void loadStatistics(
            JTextArea analyticsArea) {

        try {

            int customers =
                    getCount(
                            "SELECT COUNT(*) FROM customers"
                    );

            int waiting =
                    getCount(
                            "SELECT COUNT(*) FROM tokens "
                                    + "WHERE status='WAITING'"
                    );

            int serving =
                    getCount(
                            "SELECT COUNT(*) FROM tokens "
                                    + "WHERE status='SERVING'"
                    );

            int completed =
                    getCount(
                            "SELECT COUNT(*) FROM tokens "
                                    + "WHERE status='COMPLETED'"
                    );

            customersLabel.setText(
                    String.valueOf(customers)
            );

            waitingLabel.setText(
                    String.valueOf(waiting)
            );

            servingLabel.setText(
                    String.valueOf(serving)
            );

            completedLabel.setText(
                    String.valueOf(completed)
            );

            analyticsArea.setText(
                    "SMARTQUEUE REAL-TIME ANALYTICS\n"
                            + "====================================\n\n"
                            + "Total Customers : "
                            + customers
                            + "\n"
                            + "Waiting Tokens   : "
                            + waiting
                            + "\n"
                            + "Serving Tokens   : "
                            + serving
                            + "\n"
                            + "Completed Tokens : "
                            + completed
                            + "\n\n"
                            + "Database Status  : CONNECTED\n"
                            + "System Status    : RUNNING\n"
            );

        } catch (Exception e) {

            analyticsArea.setText(
                    "Unable to load analytics.\n\n"
                            + e.getMessage()
            );

            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DATABASE COUNT
    // =========================================================

    private int getCount(String sql)
            throws Exception {

        try (
                Connection connection =
                        util.DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }

        return 0;
    }

    // =========================================================
    // MANAGE USERS
    // =========================================================

    private void showUsers() {

        String sql =
                "SELECT user_id, username, full_name, "
                        + "email, phone, role, is_active "
                        + "FROM users "
                        + "ORDER BY user_id";

        try {

            Connection connection =
                    util.DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery();

            ResultSetTableModel model =
                    new ResultSetTableModel(
                            resultSet
                    );

            JTable table =
                    new JTable(model);

            table.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            table.setAutoResizeMode(
                    JTable.AUTO_RESIZE_ALL_COLUMNS
            );

            JScrollPane scrollPane =
                    new JScrollPane(table);

            JDialog dialog =
                    new JDialog(
                            frame,
                            "Manage Users",
                            true
                    );

            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(frame);
            dialog.setLayout(
                    new BorderLayout()
            );

            dialog.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            // =================================================
            // BOTTOM BUTTONS
            // =================================================

            JPanel bottomPanel =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT
                            )
                    );

            JButton deleteButton =
                    new JButton(
                            "Delete Selected User"
                    );

            JButton closeButton =
                    new JButton("Close");

            bottomPanel.add(deleteButton);
            bottomPanel.add(closeButton);

            dialog.add(
                    bottomPanel,
                    BorderLayout.SOUTH
            );

            // =================================================
            // DELETE USER
            // =================================================

            deleteButton.addActionListener(e -> {

                int selectedRow =
                        table.getSelectedRow();

                if (selectedRow == -1) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Please select a user first.",
                            "Delete User",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                int selectedModelRow =
                        table.convertRowIndexToModel(
                                selectedRow
                        );

                int selectedUserId =
                        Integer.parseInt(
                                table.getModel()
                                        .getValueAt(
                                                selectedModelRow,
                                                0
                                        )
                                        .toString()
                        );

                String username =
                        table.getModel()
                                .getValueAt(
                                        selectedModelRow,
                                        1
                                )
                                .toString();

                String role =
                        table.getModel()
                                .getValueAt(
                                        selectedModelRow,
                                        5
                                )
                                .toString();

                // Protect logged-in admin
                if (selectedUserId == userId) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "You cannot delete the currently "
                                    + "logged-in admin.",
                            "Delete User",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                int confirmation =
                        JOptionPane.showConfirmDialog(
                                dialog,
                                "Delete user '"
                                        + username
                                        + "' permanently?",
                                "Confirm Delete",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                if (confirmation !=
                        JOptionPane.YES_OPTION) {

                    return;
                }

                try {

                    deleteUser(
                            selectedUserId,
                            role
                    );

                    JOptionPane.showMessageDialog(
                            dialog,
                            "User deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dialog.dispose();

                    // Reopen refreshed table
                    showUsers();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Unable to delete user.\n\n"
                                    + ex.getMessage(),
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            closeButton.addActionListener(
                    e -> dialog.dispose()
            );

            dialog.setVisible(true);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    private void deleteUser(
            int selectedUserId,
            String role) throws Exception {

        Connection connection =
                util.DBConnection.getConnection();

        try {

            connection.setAutoCommit(false);

            // =================================================
            // CUSTOMER
            // =================================================

            if ("CUSTOMER".equalsIgnoreCase(role)) {

                int customerId = -1;

                String findCustomerSql =
                        "SELECT customer_id "
                                + "FROM customers "
                                + "WHERE user_id = ?";

                try (
                        PreparedStatement statement =
                                connection.prepareStatement(
                                        findCustomerSql
                                )
                ) {

                    statement.setInt(
                            1,
                            selectedUserId
                    );

                    try (
                            ResultSet resultSet =
                                    statement.executeQuery()
                    ) {

                        if (resultSet.next()) {

                            customerId =
                                    resultSet.getInt(
                                            "customer_id"
                                    );
                        }
                    }
                }

                if (customerId > 0) {

                    // -----------------------------------------
                    // Find customer tokens
                    // -----------------------------------------

                    List<Long> tokenIds =
                            new ArrayList<>();

                    String tokenSql =
                            "SELECT token_id "
                                    + "FROM tokens "
                                    + "WHERE customer_id = ?";

                    try (
                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            tokenSql
                                    )
                    ) {

                        statement.setInt(
                                1,
                                customerId
                        );

                        try (
                                ResultSet resultSet =
                                        statement.executeQuery()
                        ) {

                            while (resultSet.next()) {

                                tokenIds.add(
                                        resultSet.getLong(
                                                "token_id"
                                        )
                                );
                            }
                        }
                    }

                    // -----------------------------------------
                    // Delete queue history
                    // -----------------------------------------

                    for (Long tokenId : tokenIds) {

                        try (
                                PreparedStatement statement =
                                        connection.prepareStatement(
                                                "DELETE FROM queue_history "
                                                        + "WHERE token_id = ?"
                                        )
                        ) {

                            statement.setLong(
                                    1,
                                    tokenId
                            );

                            statement.executeUpdate();
                        }
                    }

                    // -----------------------------------------
                    // Delete queue entries
                    // -----------------------------------------

                    for (Long tokenId : tokenIds) {

                        try (
                                PreparedStatement statement =
                                        connection.prepareStatement(
                                                "DELETE FROM queue_entries "
                                                        + "WHERE token_id = ?"
                                        )
                        ) {

                            statement.setLong(
                                    1,
                                    tokenId
                            );

                            statement.executeUpdate();
                        }
                    }

                    // -----------------------------------------
                    // Delete tokens
                    // -----------------------------------------

                    try (
                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            "DELETE FROM tokens "
                                                    + "WHERE customer_id = ?"
                                    )
                    ) {

                        statement.setInt(
                                1,
                                customerId
                        );

                        statement.executeUpdate();
                    }

                    // -----------------------------------------
                    // Delete appointments
                    // -----------------------------------------

                    try (
                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            "DELETE FROM appointments "
                                                    + "WHERE customer_id = ?"
                                    )
                    ) {

                        statement.setInt(
                                1,
                                customerId
                        );

                        statement.executeUpdate();
                    }

                    // -----------------------------------------
                    // Delete customer
                    // -----------------------------------------

                    try (
                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            "DELETE FROM customers "
                                                    + "WHERE customer_id = ?"
                                    )
                    ) {

                        statement.setInt(
                                1,
                                customerId
                        );

                        statement.executeUpdate();
                    }
                }
            }

            // =================================================
            // STAFF
            // =================================================

            if ("STAFF".equalsIgnoreCase(role)) {

                try (
                        PreparedStatement statement =
                                connection.prepareStatement(
                                        "DELETE FROM staff "
                                                + "WHERE user_id = ?"
                                )
                ) {

                    statement.setInt(
                            1,
                            selectedUserId
                    );

                    statement.executeUpdate();
                }
            }

            // =================================================
            // DELETE USER
            // =================================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    "DELETE FROM users "
                                            + "WHERE user_id = ?"
                            )
            ) {

                statement.setInt(
                        1,
                        selectedUserId
                );

                int affectedRows =
                        statement.executeUpdate();

                if (affectedRows == 0) {

                    throw new Exception(
                            "User was not found."
                    );
                }
            }

            connection.commit();

        } catch (Exception e) {

            connection.rollback();

            throw e;

        } finally {

            connection.setAutoCommit(true);
            connection.close();
        }
    }

    // =========================================================
    // MANAGE COUNTERS
    // =========================================================

    private void showCounters() {

        String sql =
                "SELECT * FROM counters";

        showTable(
                "Manage Counters",
                sql
        );
    }

    // =========================================================
    // MANAGE SERVICES
    // =========================================================

    private void showServices() {

        String sql =
                "SELECT * FROM services";

        showTable(
                "Manage Services",
                sql
        );
    }

    // =========================================================
    // SHOW TABLE
    // =========================================================

    private void showTable(
            String title,
            String sql) {

        try {

            Connection connection =
                    util.DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery();

            ResultSetTableModel model =
                    new ResultSetTableModel(
                            resultSet
                    );

            JTable table =
                    new JTable(model);

            table.setAutoResizeMode(
                    JTable.AUTO_RESIZE_ALL_COLUMNS
            );

            JScrollPane scrollPane =
                    new JScrollPane(table);

            JDialog dialog =
                    new JDialog(
                            frame,
                            title,
                            true
                    );

            dialog.setSize(850, 450);
            dialog.setLocationRelativeTo(frame);
            dialog.setLayout(
                    new BorderLayout()
            );

            dialog.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            JButton closeButton =
                    new JButton("Close");

            closeButton.addActionListener(
                    e -> dialog.dispose()
            );

            JPanel bottom =
                    new JPanel();

            bottom.add(closeButton);

            dialog.add(
                    bottom,
                    BorderLayout.SOUTH
            );

            dialog.setVisible(true);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        if (authenticationService != null) {

            authenticationService.logout();
        }

        new LoginView(
                frame,
                authenticationService
        ).show();
    }

    // =========================================================
    // RESULT SET TABLE MODEL
    // =========================================================

    private static class ResultSetTableModel
            extends javax.swing.table.AbstractTableModel {

        private final List<String> columnNames =
                new ArrayList<>();

        private final List<Object[]> rows =
                new ArrayList<>();

        ResultSetTableModel(
                ResultSet resultSet)
                throws Exception {

            int columnCount =
                    resultSet.getMetaData()
                            .getColumnCount();

            for (int i = 1;
                 i <= columnCount;
                 i++) {

                columnNames.add(
                        resultSet.getMetaData()
                                .getColumnName(i)
                );
            }

            while (resultSet.next()) {

                Object[] row =
                        new Object[columnCount];

                for (int i = 0;
                     i < columnCount;
                     i++) {

                    row[i] =
                            resultSet.getObject(
                                    i + 1
                            );
                }

                rows.add(row);
            }

            resultSet.close();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.size();
        }

        @Override
        public String getColumnName(
                int column) {

            return columnNames.get(column);
        }

        @Override
        public Object getValueAt(
                int row,
                int column) {

            return rows.get(row)[column];
        }
    }
}
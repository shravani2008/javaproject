package ui;

import model.QueueEntry;
import service.AuthenticationService;
import service.QueueService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffDashboard {

    private final JFrame frame;
    private final int userId;
    private final String name;
    private final AuthenticationService authenticationService;
    private final QueueService queueService;

    private QueueEntry currentEntry;

    private JLabel tokenLabel;
    private JLabel statusLabel;
    private JLabel waitingLabel;

    private JButton callNextButton;
    private JButton startButton;
    private JButton completeButton;
    private JButton skipButton;

    private Timer refreshTimer;

    public StaffDashboard(
            JFrame frame,
            int userId,
            String name,
            AuthenticationService authenticationService) {

        this.frame = frame;
        this.userId = userId;
        this.name = name;
        this.authenticationService = authenticationService;
        this.queueService = new QueueService();
    }

    public void show() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 247, 250));

        // HEADER
        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(37, 99, 235));

        header.setBorder(
                new EmptyBorder(15, 20, 15, 20));

        JLabel title =
                new JLabel("SMARTQUEUE | STAFF");

        title.setForeground(Color.WHITE);
        title.setFont(
                new Font("Arial", Font.BOLD, 24));

        JLabel welcome =
                new JLabel("Welcome, " + name);

        welcome.setForeground(Color.WHITE);

        JButton logoutButton =
                new JButton("LOGOUT");

        logoutButton.addActionListener(
                e -> logout());

        JPanel rightHeader =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT));

        rightHeader.setOpaque(false);

        rightHeader.add(welcome);
        rightHeader.add(logoutButton);

        header.add(
                title,
                BorderLayout.WEST);

        header.add(
                rightHeader,
                BorderLayout.EAST);

        // CENTER
        JPanel center =
                new JPanel();

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS));

        center.setBackground(
                new Color(245, 247, 250));

        center.setBorder(
                new EmptyBorder(
                        40, 100, 40, 100));

        JLabel currentTitle =
                new JLabel("CURRENT CUSTOMER");

        currentTitle.setFont(
                new Font("Arial",
                        Font.BOLD,
                        18));

        currentTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        tokenLabel =
                new JLabel("NO CUSTOMER");

        tokenLabel.setFont(
                new Font("Arial",
                        Font.BOLD,
                        42));

        tokenLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        statusLabel =
                new JLabel("IDLE");

        statusLabel.setFont(
                new Font("Arial",
                        Font.BOLD,
                        18));

        statusLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        waitingLabel =
                new JLabel("Waiting: 0");

        waitingLabel.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        16));

        waitingLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        center.add(currentTitle);
        center.add(
                Box.createVerticalStrut(20));

        center.add(tokenLabel);
        center.add(
                Box.createVerticalStrut(10));

        center.add(statusLabel);
        center.add(
                Box.createVerticalStrut(10));

        center.add(waitingLabel);
        center.add(
                Box.createVerticalStrut(35));

        // BUTTONS
        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                2, 2, 15, 15));

        buttons.setOpaque(false);

        callNextButton =
                new JButton("CALL NEXT");

        startButton =
                new JButton("START SERVICE");

        completeButton =
                new JButton("COMPLETE SERVICE");

        skipButton =
                new JButton("SKIP CUSTOMER");

        buttons.add(callNextButton);
        buttons.add(startButton);
        buttons.add(completeButton);
        buttons.add(skipButton);

        center.add(buttons);

        callNextButton.addActionListener(
                e -> callNext());

        startButton.addActionListener(
                e -> startService());

        completeButton.addActionListener(
                e -> completeService());

        skipButton.addActionListener(
                e -> skipCustomer());

        mainPanel.add(
                header,
                BorderLayout.NORTH);

        mainPanel.add(
                center,
                BorderLayout.CENTER);

        frame.setTitle(
                "SmartQueue | Staff Dashboard");

        frame.setContentPane(mainPanel);

        frame.setSize(900, 600);

        frame.setResizable(false);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        updateButtons();
        refreshWaitingCount();

        refreshTimer =
                new Timer(
                        3000,
                        e -> refreshWaitingCount());

        refreshTimer.start();
    }

    private void callNext() {

        try {

            QueueEntry entry =
                    queueService.callNext();

            if (entry == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "No customers are waiting.",
                        "Queue",
                        JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            currentEntry = entry;

            currentEntry.setStatus(
                    QueueEntry.Status.CALLED);

            updateDisplay();

            JOptionPane.showMessageDialog(
                    frame,
                    "Next customer has been called.",
                    "Customer Called",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    private void startService() {

        if (currentEntry == null) {
            return;
        }

        try {

            boolean success =
                    queueService.startServing(
                            currentEntry.getTokenId());

            if (success) {

                currentEntry.setStatus(
                        QueueEntry.Status.SERVING);

                updateDisplay();
            }

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    private void completeService() {

        if (currentEntry == null) {
            return;
        }

        try {

            boolean success =
                    queueService.completeService(
                            currentEntry.getTokenId());

            if (success) {

                currentEntry.setStatus(
                        QueueEntry.Status.COMPLETED);

                currentEntry = null;

                updateDisplay();
                refreshWaitingCount();
            }

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    private void skipCustomer() {

        if (currentEntry == null) {
            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        frame,
                        "Skip this customer?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean success =
                    queueService.skipCustomer(
                            currentEntry.getTokenId());

            if (success) {

                currentEntry.setStatus(
                        QueueEntry.Status.SKIPPED);

                currentEntry = null;

                updateDisplay();
                refreshWaitingCount();
            }

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    private void updateDisplay() {

        if (currentEntry == null) {

            tokenLabel.setText("NO CUSTOMER");
            statusLabel.setText("IDLE");

        } else {

            tokenLabel.setText(
                    "TOKEN #" +
                    currentEntry.getTokenId());

            statusLabel.setText(
                    currentEntry.getStatus().name());
        }

        updateButtons();
    }

    private void updateButtons() {

        boolean hasCustomer =
                currentEntry != null;

        boolean called =
                hasCustomer &&
                currentEntry.getStatus()
                        == QueueEntry.Status.CALLED;

        boolean serving =
                hasCustomer &&
                currentEntry.getStatus()
                        == QueueEntry.Status.SERVING;

        callNextButton.setEnabled(
                !hasCustomer);

        startButton.setEnabled(called);

        completeButton.setEnabled(serving);

        skipButton.setEnabled(hasCustomer);
    }

    private void refreshWaitingCount() {

        try {

            int count =
                    queueService.getWaitingCount();

            waitingLabel.setText(
                    "Waiting: " + count);

        } catch (Exception e) {

            waitingLabel.setText(
                    "Waiting: Error");
        }
    }

    private void logout() {

        if (refreshTimer != null) {
            refreshTimer.stop();
        }

        authenticationService.logout();

        new LoginView(
                frame,
                authenticationService).show();
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                frame,
                message == null
                        ? "An error occurred."
                        : message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
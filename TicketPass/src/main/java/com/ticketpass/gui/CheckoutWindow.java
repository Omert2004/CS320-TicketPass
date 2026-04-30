package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Seat;
import com.ticketpass.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

public class CheckoutWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;
    private int eventId;
    private Seat seat;

    private JLabel lblTimer;
    private Timer countdownTimer;
    private int timeRemaining = 600;

    private JTextField txtCardNumber;
    private JTextField txtExpiry;
    private JPasswordField txtCvv;

    public CheckoutWindow(TicketPass ticketPass, User currentUser, int eventId, Seat seat) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;
        this.eventId = eventId;
        this.seat = seat;

        setTitle("TicketPass - Secure Checkout");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelAndReleaseSeat();
                System.exit(0);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Secure Checkout", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblTimer = new JLabel("Time Remaining: 10:00", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTimer.setForeground(new Color(220, 53, 69));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        headerPanel.add(lblTimer, BorderLayout.SOUTH);

        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Order Summary", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        summaryPanel.setBackground(Color.WHITE);

        JLabel lblSeatInfo = new JLabel("  Seat: Row " + seat.getRowLabel() + " - Number " + seat.getSeatNumber());
        lblSeatInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        summaryPanel.add(lblSeatInfo);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));

        formPanel.add(new JLabel("Card Number (16 digits):"));
        txtCardNumber = new JTextField();
        txtCardNumber.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(txtCardNumber);

        JPanel splitRow = new JPanel(new GridLayout(1, 2, 15, 0));

        JPanel expiryPanel = new JPanel(new BorderLayout());
        expiryPanel.add(new JLabel("Expiry (MM/YY):"), BorderLayout.NORTH);
        txtExpiry = new JTextField();
        txtExpiry.setToolTipText("Format: MM/YY");
        txtExpiry.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        expiryPanel.add(txtExpiry, BorderLayout.CENTER);

        JPanel cvvPanel = new JPanel(new BorderLayout());
        cvvPanel.add(new JLabel("CVV (3 Digits):"), BorderLayout.NORTH);
        txtCvv = new JPasswordField();
        txtCvv.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cvvPanel.add(txtCvv, BorderLayout.CENTER);

        splitRow.add(expiryPanel);
        splitRow.add(cvvPanel);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(splitRow);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton btnCancel = new JButton("Cancel");
        JButton btnPay = new JButton("Complete Payment");

        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(150, 40));

        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setPreferredSize(new Dimension(200, 40));
        btnPay.setBackground(new Color(40, 167, 69));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnPay);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.add(summaryPanel, BorderLayout.NORTH);
        centerContainer.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnCancel.addActionListener(e -> {
            cancelAndReleaseSeat();
            new SeatingChartWindow(ticketPass, currentUser, eventId).setVisible(true);
            dispose();
        });

        btnPay.addActionListener(e -> validateAndProcess());

        startCountdownTimer();
    }

    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            lblTimer.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));

            if (timeRemaining <= 0) {
                countdownTimer.stop();
                handleTimeout();
            }
        });
        countdownTimer.start();
    }

    private void handleTimeout() {
        ticketPass.releaseSeatLock(seat.getSeatId());
        JOptionPane.showMessageDialog(this, "Time expired! Your seat lock has been released.", "Timeout", JOptionPane.WARNING_MESSAGE);
        new SeatingChartWindow(ticketPass, currentUser, eventId).setVisible(true);
        dispose();
    }

    private void cancelAndReleaseSeat() {
        if (countdownTimer != null) countdownTimer.stop();
        ticketPass.releaseSeatLock(seat.getSeatId());
    }

    private void validateAndProcess() {
        String cardNumber = txtCardNumber.getText().replaceAll("\\s+", "");
        String expiry = txtExpiry.getText().trim();
        String cvv = new String(txtCvv.getPassword()).trim();

        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in MM/YY format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this, "CVV must be exactly 3 digits.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lastFour = cardNumber.substring(12);
        String paymentToken = "tok_" + UUID.randomUUID().toString();

        int transactionId = ticketPass.processPayment(currentUser.getUserId(), lastFour, paymentToken, "SUCCESS");

        if (transactionId > 0) {
            int ticketId = ticketPass.generateTicketFlow(currentUser.getUserId(), eventId, seat.getSeatId(), transactionId);

            if (ticketId > 0) {
                countdownTimer.stop();
                new TicketSuccessWindow(ticketPass, currentUser, ticketId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Payment successful, but ticket generation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Payment processing failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
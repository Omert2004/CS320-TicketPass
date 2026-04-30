package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.User;
import com.ticketpass.model.Seat;
import com.ticketpass.model.dto.SeatingChart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class SeatingChartWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;
    private int eventId;
    private Seat selectedSeat = null;

    private JPanel chartPanel;
    private JButton btnProceed;
    private JLabel lblSelectedInfo;

    private final Color COLOR_AVAILABLE = new Color(40, 167, 69);
    private final Color COLOR_LOCKED = new Color(255, 193, 7);
    private final Color COLOR_SOLD = new Color(108, 117, 125);
    private final Color COLOR_SELECTED = new Color(0, 123, 255);

    public SeatingChartWindow(TicketPass ticketPass, User currentUser, int eventId) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;
        this.eventId = eventId;

        setTitle("TicketPass - Select Your Seat");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Interactive Seating Chart", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.add(createLegendItem("Available", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("In Cart/Locked", COLOR_LOCKED));
        legendPanel.add(createLegendItem("Sold", COLOR_SOLD));
        legendPanel.add(createLegendItem("Your Selection", COLOR_SELECTED));

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(legendPanel, BorderLayout.SOUTH);

        chartPanel = new JPanel();
        chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
        chartPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblSelectedInfo = new JLabel("No seat selected");
        lblSelectedInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        JPanel buttonActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnBack = new JButton("Back");
        btnProceed = new JButton("Proceed to Checkout");

        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProceed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProceed.setBackground(new Color(40, 167, 69));
        btnProceed.setForeground(Color.WHITE);
        btnProceed.setEnabled(false);

        buttonActions.add(btnBack);
        buttonActions.add(btnProceed);

        bottomPanel.add(lblSelectedInfo, BorderLayout.WEST);
        bottomPanel.add(buttonActions, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnBack.addActionListener(e -> {
            new EventDetailsWindow(ticketPass, currentUser, eventId).setVisible(true);
            dispose();
        });

        btnProceed.addActionListener(e -> {
            if (selectedSeat != null) {
                boolean success = ticketPass.selectSeat(selectedSeat.getSeatId(), currentUser.getUserId());

                if (success) {
                    new CheckoutWindow(ticketPass, currentUser, eventId, selectedSeat).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Sorry, this seat was just locked by another user!", "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
                    loadSeatingChart();
                }
            }
        });

        loadSeatingChart();
    }

    private void loadSeatingChart() {
        chartPanel.removeAll();
        selectedSeat = null;
        btnProceed.setEnabled(false);
        lblSelectedInfo.setText("No seat selected");

        JLabel lblStage = new JLabel("STAGE");
        lblStage.setOpaque(true);
        lblStage.setBackground(new Color(230, 230, 230));
        lblStage.setHorizontalAlignment(SwingConstants.CENTER);
        lblStage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStage.setMaximumSize(new Dimension(400, 40));
        lblStage.setAlignmentX(Component.CENTER_ALIGNMENT);

        chartPanel.add(Box.createVerticalStrut(20));
        chartPanel.add(lblStage);
        chartPanel.add(Box.createVerticalStrut(30));

        SeatingChart chartData = ticketPass.getSeatingChart(eventId);

        if (chartData == null || chartData.getSeats() == null || chartData.getSeats().isEmpty()) {
            chartPanel.add(new JLabel("No seating data available for this venue."));
            refreshUI();
            return;
        }

        List<Seat> seats = chartData.getSeats();
        String currentRowLabel = "";
        JPanel rowPanel = null;

        for (Seat seat : seats) {
            if (!seat.getRowLabel().equals(currentRowLabel)) {
                currentRowLabel = seat.getRowLabel();
                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
                rowPanel.setBackground(Color.WHITE);

                JLabel lblRow = new JLabel("Row " + currentRowLabel);
                lblRow.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblRow.setPreferredSize(new Dimension(50, 30));
                rowPanel.add(lblRow);

                chartPanel.add(rowPanel);
            }

            JButton btnSeat = new JButton(String.valueOf(seat.getSeatNumber()));
            btnSeat.setPreferredSize(new Dimension(50, 45));
            btnSeat.setFocusPainted(false);
            btnSeat.setFont(new Font("Segoe UI", Font.BOLD, 11));

            switch (seat.getStatus()) {
                case AVAILABLE:
                    btnSeat.setBackground(COLOR_AVAILABLE);
                    btnSeat.setForeground(Color.WHITE);
                    btnSeat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnSeat.addActionListener(e -> {
                        loadSeatingChartUIOnly(seats, seat);
                        selectedSeat = seat;
                        btnProceed.setEnabled(true);
                        lblSelectedInfo.setText("Selected: Row " + seat.getRowLabel() + " - Seat " + seat.getSeatNumber());
                    });
                    break;
                case LOCKED:
                    btnSeat.setBackground(COLOR_LOCKED);
                    btnSeat.setEnabled(false);
                    btnSeat.setToolTipText("Currently in someone's cart");
                    break;
                case SOLD:
                case BLOCKED:
                    btnSeat.setBackground(COLOR_SOLD);
                    btnSeat.setEnabled(false);
                    break;
            }
            rowPanel.add(btnSeat);
        }
        refreshUI();
    }

    private void loadSeatingChartUIOnly(List<Seat> allSeats, Seat currentSelection) {
        Component[] rows = chartPanel.getComponents();
        for (Component row : rows) {
            if (row instanceof JPanel) {
                Component[] buttons = ((JPanel) row).getComponents();
                for (Component b : buttons) {
                    if (b instanceof JButton) {
                        JButton btn = (JButton) b;
                        if (btn.getText().equals(String.valueOf(currentSelection.getSeatNumber())) &&
                                ((JLabel)((JPanel)row).getComponent(0)).getText().contains(currentSelection.getRowLabel())) {
                            btn.setBackground(COLOR_SELECTED);
                        } else {
                            if (btn.isEnabled() && btn.getBackground().equals(COLOR_SELECTED)) {
                                btn.setBackground(COLOR_AVAILABLE);
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(15, 15));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(box);
        panel.add(new JLabel(text));
        return panel;
    }

    private void refreshUI() {
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
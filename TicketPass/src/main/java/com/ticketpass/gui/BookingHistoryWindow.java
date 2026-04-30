package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.User;
import com.ticketpass.model.dto.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingHistoryWindow extends JFrame {
    private TicketPass ticketPass;
    private User currentUser;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public BookingHistoryWindow(TicketPass ticketPass, User currentUser) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;

        setTitle("TicketPass - My Booking History");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Your Purchase History");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Ticket ID", "Event Name", "Event Date", "Venue", "Address", "Seat", "Purchased On"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingTable.setRowHeight(35);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bookingTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
        bookingTable.getColumnModel().getColumn(0).setWidth(0);

        mainPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnBack = new JButton("Back to Dashboard");
        JButton btnViewTicket = new JButton("View Ticket Details");

        btnViewTicket.setBackground(new Color(0, 123, 255));
        btnViewTicket.setForeground(Color.WHITE);
        btnViewTicket.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnViewTicket.setFocusPainted(false);

        bottomPanel.add(btnBack);
        bottomPanel.add(btnViewTicket);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnBack.addActionListener(e -> {
            new CustomerDashboardWindow(ticketPass, currentUser).setVisible(true);
            dispose();
        });

        btnViewTicket.addActionListener(e -> {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow >= 0) {
                int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
                new TicketSuccessWindow(ticketPass, currentUser, ticketId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from the list.");
            }
        });

        loadHistory();
    }

    private void loadHistory() {
        tableModel.setRowCount(0);

        List<Booking> bookings = ticketPass.viewBookingHistory(currentUser.getUserId());

        DateTimeFormatter eventFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
        DateTimeFormatter purchaseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getTicketId(),
                    b.getEventName(),
                    b.getEventDate().format(eventFormatter),
                    b.getVenueName(),
                    b.getAddress(),
                    "Row " + b.getRowLabel() + " / Seat " + b.getSeatNumber(),
                    b.getPurchaseTime().format(purchaseFormatter)
            });
        }
    }
}
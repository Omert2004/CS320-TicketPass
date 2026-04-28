package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Event;
import com.ticketpass.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerDashboardWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;
    private JTable eventTable;
    private DefaultTableModel tableModel;

    public CustomerDashboardWindow(TicketPass ticketPass, User currentUser) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;

        setTitle("TicketPass - Customer Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel lblWelcome = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnSearchEvents = new JButton("Search Events");
        JButton btnMyBookings = new JButton("My Bookings");
        JButton btnLogout = new JButton("Logout");

        btnSearchEvents.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMyBookings.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));

        navButtonsPanel.add(btnSearchEvents);
        navButtonsPanel.add(btnMyBookings);
        navButtonsPanel.add(btnLogout);

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(navButtonsPanel, BorderLayout.EAST);

        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        JLabel lblSectionTitle = new JLabel("Upcoming Events");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String[] columnNames = {"Event ID", "Name", "Category", "Date", "Venue", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventTable = new JTable(tableModel);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventTable.setRowHeight(30);
        eventTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        eventTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(eventTable);

        contentPanel.add(lblSectionTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnViewDetails = new JButton("View Event Details & Book");
        btnViewDetails.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnViewDetails.setPreferredSize(new Dimension(250, 40));

        bottomPanel.add(btnViewDetails);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnLogout.addActionListener(e -> {
            new LoginWindow(ticketPass).setVisible(true);
            dispose();
        });

        btnViewDetails.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow >= 0) {
                int selectedEventId = (int) tableModel.getValueAt(selectedRow, 0);
                // TODO: EventDetailsWindow
                //new EventDetailsWindow(ticketPass, currentUser, selectedEventId).setVisible(true);
                //dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event from the list first!", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSearchEvents.addActionListener(e -> {
            // TODO: EventSearchWindow
            //new EventSearchWindow(ticketPass, currentUser).setVisible(true);
            //dispose();
        });

        btnMyBookings.addActionListener(e -> {
            // TODO: BookingHistoryWindow
        });

        loadUpcomingEvents();
    }

    private void loadUpcomingEvents() {
        tableModel.setRowCount(0); // Clear existing rows

        List<Event> upcomingEvents = ticketPass.getUpcomingEvents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");

        for (Event event : upcomingEvents) {
            Object[] row = {
                    event.getEventId(),
                    event.getName(),
                    event.getCategory(),
                    event.getEventDate().format(formatter),
                    event.getVenueName(),
                    String.format("$%.2f", event.getPrice())
            };
            tableModel.addRow(row);
        }
    }
}
package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Event;
import com.ticketpass.model.User;
import com.ticketpass.model.dto.EventStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrganizerDashboardWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;

    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JButton btnAddEvent;
    private JButton btnEditEvent;
    private JButton btnCancelEvent;
    private JButton btnViewStats;
    private JTextArea reportTextArea;

    public OrganizerDashboardWindow(TicketPass ticketPass, User currentUser) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;

        setTitle("TicketPass - Organizer Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        layoutComponents();
        attachListeners();
        loadOrganizerEvents();
    }

    private void initComponents() {
        // Table for Events
        String[] columns = {"Event ID", "Name", "Category", "Date", "Venue", "Capacity", "Price", "Status"};

        // Custom TableModel that disables double-click editing
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This makes ALL cells strictly read-only!
            }
        };
        eventTable = new JTable(tableModel);

        // Buttons
        btnAddEvent = new JButton("Add New Event");
        btnEditEvent = new JButton("Edit Selected Event");
        btnCancelEvent = new JButton("Cancel Selected Event");
        btnViewStats = new JButton("Generate Sales Report");

        // Reporting Area
        reportTextArea = new JTextArea(10, 30);
        reportTextArea.setEditable(false);
        reportTextArea.setBorder(BorderFactory.createTitledBorder("Event Statistics & Reports"));
    }

    private void layoutComponents() {
        // Top Panel with Buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnAddEvent);
        topPanel.add(btnEditEvent);
        topPanel.add(btnCancelEvent);
        topPanel.add(btnViewStats);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Table
        JScrollPane tableScrollPane = new JScrollPane(eventTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("My Managed Events"));
        add(tableScrollPane, BorderLayout.CENTER);

        // Bottom Panel for Reports
        JScrollPane reportScrollPane = new JScrollPane(reportTextArea);
        add(reportScrollPane, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        //Add Event Listener (SRS-TP-005)
        btnAddEvent.addActionListener(e -> openAddEventDialog());
        //Edit Event Listener (SRS-TP-005)
        btnEditEvent.addActionListener(e -> openEditEventDialog());
        //Cancel Event Listener (SRS-TP-005)
        btnCancelEvent.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this event?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.cancelEvent(currentUser, eventId);
                    JOptionPane.showMessageDialog(this, "Event Cancelled Successfully!");

                    // Refresh table
                    loadOrganizerEvents();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        //View Stats Listener (SRS-TP-008)
        btnViewStats.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) tableModel.getValueAt(selectedRow, 0);

                EventStats stats = ticketPass.viewEventStatistics(currentUser, eventId);

                if (stats != null) {
                    reportTextArea.setText("--- Sales Report for Event ID: " + eventId + " ---\n" +
                            "Venue Capacity: " + stats.getVenueCapacity() + "\n" +
                            "Seats Purchased: " + stats.getSeatsPurchased() + "\n" +
                            "Occupancy Rate: " + stats.getOccupancyRate() + "%\n" +
                            "Expected Revenue: $" + stats.getExpectedRevenue() + "\n" +
                            "-----------------------------------");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event to view its statistics.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Opens a basic form to collect Event Data
    private void openAddEventDialog() {
        JTextField txtName = new JTextField();
        JTextField txtCategory = new JTextField();
        JTextField txtVenue = new JTextField();
        JTextField txtCapacity = new JTextField();
        JTextField txtPrice = new JTextField();

        Object[] message = {
                "Event Name:", txtName,
                "Category:", txtCategory,
                "Venue Name:", txtVenue,
                "Capacity:", txtCapacity,
                "Price:", txtPrice
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // Bundle data into Event Model
                Event newEvent = new Event();
                newEvent.setName(txtName.getText());
                newEvent.setCategory(txtCategory.getText());
                newEvent.setVenueName(txtVenue.getText());
                newEvent.setVenueCapacity(Integer.parseInt(txtCapacity.getText()));
                newEvent.setPrice(Double.parseDouble(txtPrice.getText()));
                // Set default date for demonstration (should be added to form)
                newEvent.setEventDate(LocalDateTime.now().plusMonths(1));

                // Send to backend
                ticketPass.createNewEvent(currentUser, newEvent);

                JOptionPane.showMessageDialog(this, "Event Created Successfully!");
                // Refresh table
                loadOrganizerEvents();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Capacity and Price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void openEditEventDialog() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit.");
            return;
        }

        // 1. Grab current values from the Correct Columns
        // 0:ID, 1:Name, 2:Category, 3:Date, 4:Venue, 5:Capacity, 6:Price, 7:Status
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentCategory = (String) tableModel.getValueAt(selectedRow, 2);
        String currentDateStr = (String) tableModel.getValueAt(selectedRow, 3);
        String currentVenue = (String) tableModel.getValueAt(selectedRow, 4);

        // Safety check for Capacity (Column 5)
        Object capVal = tableModel.getValueAt(selectedRow, 5);
        String currentCapacity = (capVal != null) ? capVal.toString() : "0";

        // Safety check for Price (Column 6) - Remove $ for display
        String currentPriceStr = tableModel.getValueAt(selectedRow, 6).toString().replace("$", "").trim();

        // 2. Initialize UI Components
        JTextField txtName = new JTextField(currentName);
        JTextField txtDate = new JTextField(currentDateStr);
        JTextField txtVenue = new JTextField(currentVenue);
        JTextField txtCapacity = new JTextField(currentCapacity);
        JTextField txtPrice = new JTextField(currentPriceStr);

        JComboBox<String> comboCategory = new JComboBox<>(new String[]{"Music", "Theater", "Sports", "Seminar", "Other"});
        comboCategory.setSelectedItem(currentCategory);

        JComboBox<com.ticketpass.model.EventStatus> comboStatus = new JComboBox<>(com.ticketpass.model.EventStatus.values());
        comboStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7));

        Object[] message = {
                "Event Name:", txtName,
                "Category:", comboCategory,
                "Status:", comboStatus,
                "Date:", txtDate,
                "Venue:", txtVenue,
                "Capacity:", txtCapacity,
                "Price:", txtPrice
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Event", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Event updatedData = new Event();
                updatedData.setName(txtName.getText().trim());
                updatedData.setCategory((String) comboCategory.getSelectedItem());
                updatedData.setStatus((com.ticketpass.model.EventStatus) comboStatus.getSelectedItem());
                updatedData.setAddress(txtVenue.getText().trim());

                // 1. Parse BOTH as Double first to be safe (handles "100.0" or "100")
                double priceVal = Double.parseDouble(txtPrice.getText().replace("$", "").trim());
                double capacityVal = Double.parseDouble(txtCapacity.getText().trim());

                // 2. Set them (Casting capacity to int since seats must be whole numbers)
                updatedData.setPrice(priceVal);
                updatedData.setVenueCapacity((int) capacityVal);

                // 3. Date Parsing
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
                updatedData.setEventDate(LocalDateTime.parse(txtDate.getText().trim(), formatter));

                // 4. Send to Backend
                ticketPass.updateEvent(currentUser, eventId, updatedData);

                JOptionPane.showMessageDialog(this, "Success: Event Updated!");
                loadOrganizerEvents();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price or Capacity has invalid characters: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

    }

    private void loadOrganizerEvents() {
        //Clear any existing rows to prevent duplicates if refreshed
        tableModel.setRowCount(0);

        //Fetch all upcoming events
        List<Event> allEvents = ticketPass.getUpcomingEvents();

        //Match the formatting the customer dashboard uses
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");

        //Loop through and filter for ONLY this organizer
        for (Event event : allEvents) {

            // Check if the event's organizer ID matches the logged-in user's ID
            if (event.getOrganizerId() == currentUser.getUserId()) {

                Object[] row = {
                        event.getEventId(),
                        event.getName(),
                        event.getCategory(),
                        event.getEventDate().format(formatter),
                        event.getVenueName(),
                        event.getVenueCapacity(), // <--- ADDED THIS
                        String.format("$%.2f", event.getPrice()),
                        event.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }
}
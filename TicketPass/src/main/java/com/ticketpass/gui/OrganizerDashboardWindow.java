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
    private JButton btnViewSeats;
    private JButton btnViewStats;
    private JButton btnRefresh;
    private JButton btnLogout;
    private JTextArea reportTextArea;

    public OrganizerDashboardWindow(TicketPass ticketPass, User currentUser) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;

        setTitle("TicketPass - Organizer Dashboard");
        setSize(1000, 600);
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

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(tableModel);


        btnAddEvent = new JButton("Add New Event");
        btnEditEvent = new JButton("Edit Selected Event");
        btnCancelEvent = new JButton("Cancel Selected Event");
        btnViewSeats = new JButton("View/Manage Seats");
        btnViewStats = new JButton("Generate Sales Report");
        btnRefresh = new JButton("Refresh");
        btnLogout = new JButton("Logout");


        reportTextArea = new JTextArea(10, 30);
        reportTextArea.setEditable(false);
        reportTextArea.setBorder(BorderFactory.createTitledBorder("Event Statistics & Reports"));
    }

    private void layoutComponents() {

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnAddEvent);
        topPanel.add(btnEditEvent);
        topPanel.add(btnCancelEvent);
        topPanel.add(btnViewSeats);
        topPanel.add(btnViewStats);
        topPanel.add(btnRefresh);
        topPanel.add(btnLogout);
        add(topPanel, BorderLayout.NORTH);


        JScrollPane tableScrollPane = new JScrollPane(eventTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("My Managed Events"));
        add(tableScrollPane, BorderLayout.CENTER);


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

        // View/Manage Seats Listener (SRS-TP-006)
        btnViewSeats.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) tableModel.getValueAt(selectedRow, 0);
                new SeatingChartWindow(ticketPass, currentUser, eventId).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Please select an event from the table first to view its seats.", "Warning", JOptionPane.WARNING_MESSAGE);
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

        btnRefresh.addActionListener(e -> {
            loadOrganizerEvents();
        });

        btnLogout.addActionListener(e -> {
            new LoginWindow(ticketPass).setVisible(true);
            dispose();
        });
    }

    // Opens a basic form to collect Event Data
    private void openAddEventDialog() {
        JTextField txtName = new JTextField();

        JComboBox<String> comboCategory = new JComboBox<>(new String[]{"Music", "Theater", "Sports", "Seminar", "Other"});

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
        String defaultDate = LocalDateTime.now().plusMonths(1).format(formatter);
        JTextField txtDate = new JTextField(defaultDate);

        JTextField txtVenue = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtRows        = new JTextField();
        JTextField txtSeatsPerRow = new JTextField();

        Object[] message = {
                "Event Name:", txtName,
                "Category:", comboCategory,
                "Date (MMM dd, yyyy - HH:mm):", txtDate,
                "Venue:", txtVenue,
                "Address:", txtAddress,
                "Price:", txtPrice,
                "Number of Rows:", txtRows,
                "Seats Per Row:", txtSeatsPerRow
        };


        int option = JOptionPane.showConfirmDialog(this, message, "Create New Event", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Event newEvent = new Event();
                newEvent.setName(txtName.getText().trim());
                newEvent.setCategory((String) comboCategory.getSelectedItem());

                newEvent.setAddress(txtAddress.getText().trim());
                newEvent.setVenueName(txtVenue.getText().trim());

                newEvent.setStatus(com.ticketpass.model.EventStatus.PENDING);

                String rawPrice = txtPrice.getText().replace("$", "").replace(",", ".").trim();
                if (rawPrice.isEmpty()) rawPrice = "0";
                newEvent.setPrice(Double.parseDouble(rawPrice));

                int rowCount    = Integer.parseInt(txtRows.getText().trim().isEmpty() ? "0" : txtRows.getText().trim());
                int seatsPerRow = Integer.parseInt(txtSeatsPerRow.getText().trim().isEmpty() ? "0" : txtSeatsPerRow.getText().trim());
                int capacity    = rowCount * seatsPerRow;
                newEvent.setVenueCapacity(capacity);

                newEvent.setEventDate(LocalDateTime.parse(txtDate.getText().trim(), formatter));

                int newEventId = ticketPass.createNewEvent(currentUser, newEvent);

                String rawRows = txtRows.getText().trim();
                String rawSeats = txtSeatsPerRow.getText().trim();

                if (!rawRows.isEmpty() && !rawSeats.isEmpty() && newEventId > 0) {
                    rowCount    = Integer.parseInt(rawRows);
                    seatsPerRow = Integer.parseInt(rawSeats);
                    ticketPass.generateSeats(currentUser, newEventId, rowCount, seatsPerRow);
                }

                JOptionPane.showMessageDialog(this, "Event Created Successfully!");
                // Refresh tabl
                loadOrganizerEvents();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for Capacity and Price.",
                        "Invalid Number Format",
                        JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please ensure the Date matches the format: May 03, 2026 - 20:30",
                        "Invalid Date Format",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditEventDialog() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit.");
            return;
        }

        // 0:ID, 1:Name, 2:Category, 3:Date, 4:Venue, 5:Capacity, 6:Price, 7:Status
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);

        List<Event> myEvents = ticketPass.getOrganizerEvents(currentUser.getUserId());
        Event eventToEdit = myEvents.stream().filter(e -> e.getEventId() == eventId).findFirst().orElse(null);

        if (eventToEdit == null) {
            JOptionPane.showMessageDialog(this, "Error finding event details.");
            return;
        }

        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentCategory = (String) tableModel.getValueAt(selectedRow, 2);
        String currentDateStr = (String) tableModel.getValueAt(selectedRow, 3);

        Object capVal = tableModel.getValueAt(selectedRow, 5);
        String currentCapacity = (capVal != null) ? capVal.toString() : "0";
        String currentPriceStr = tableModel.getValueAt(selectedRow, 6).toString().replace("$", "").trim();

        JTextField txtName = new JTextField(currentName);
        JTextField txtDate = new JTextField(currentDateStr);
        JTextField txtVenue = new JTextField(eventToEdit.getVenueName());
        JTextField txtAddress = new JTextField(eventToEdit.getAddress());
        JTextField txtCapacity = new JTextField(currentCapacity);
        JTextField txtPrice = new JTextField(currentPriceStr);

        JComboBox<String> comboCategory = new JComboBox<>(new String[]{"Music", "Theater", "Sports", "Seminar", "Other"});
        comboCategory.setSelectedItem(currentCategory);

        JComboBox<com.ticketpass.model.EventStatus> comboStatus = new JComboBox<>(com.ticketpass.model.EventStatus.values());
        comboStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7));

        Object[] message = {
                "Event Name:", txtName,
                "Category:", comboCategory,
                "Date:", txtDate,
                "Venue Name:", txtVenue,
                "Address:", txtAddress,
                "Capacity:", txtCapacity,
                "Price:", txtPrice,
                "Status:", comboStatus
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Event", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                com.ticketpass.model.EventStatus newStatus = (com.ticketpass.model.EventStatus) comboStatus.getSelectedItem();
                if (eventToEdit.getStatus() != com.ticketpass.model.EventStatus.ACTIVE && newStatus == com.ticketpass.model.EventStatus.ACTIVE) {
                    JOptionPane.showMessageDialog(this,
                            "Organizers cannot ACTIVATE events. This requires Admin approval.",
                            "Permission Denied",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Event updatedData = new Event();
                updatedData.setName(txtName.getText().trim());
                updatedData.setCategory((String) comboCategory.getSelectedItem());
                updatedData.setStatus((com.ticketpass.model.EventStatus) comboStatus.getSelectedItem());
                updatedData.setVenueName(txtVenue.getText().trim());
                updatedData.setAddress(txtAddress.getText().trim());

                String rawPrice = txtPrice.getText().replace("$", "").replace(",", ".").trim();
                if (rawPrice.isEmpty()) rawPrice = "0";
                double priceVal = Double.parseDouble(rawPrice);

                String rawCapacity = txtCapacity.getText().replace(".", "").replace(",", "").trim();
                if (rawCapacity.isEmpty()) rawCapacity = "0";
                int capacityVal = Integer.parseInt(rawCapacity);

                updatedData.setPrice(priceVal);
                updatedData.setVenueCapacity(capacityVal);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
                updatedData.setEventDate(LocalDateTime.parse(txtDate.getText().trim(), formatter));

                ticketPass.updateEvent(currentUser, eventId, updatedData);

                JOptionPane.showMessageDialog(this, "Success: Event Updated!");
                loadOrganizerEvents();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this,
                        "Please ensure Price and Capacity only contain numbers and decimals.\nError details: " + ex.getMessage(),
                        "Invalid Number Format",
                        JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {

                JOptionPane.showMessageDialog(this,
                        "Please ensure the Date matches the format: May 03, 2026 - 20:30",
                        "Invalid Date Format",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadOrganizerEvents() {

        tableModel.setRowCount(0);

        List<Event> myEvents = ticketPass.getOrganizerEvents(currentUser.getUserId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");

        for (Event event : myEvents) {
            Object[] row = {
                    event.getEventId(),
                    event.getName(),
                    event.getCategory(),
                    event.getEventDate().format(formatter),
                    event.getVenueName(),
                    event.getVenueCapacity(),
                    String.format("$%.2f", event.getPrice()),
                    event.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}
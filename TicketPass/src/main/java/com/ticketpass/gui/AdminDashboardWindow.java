package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Event;
import com.ticketpass.model.User;
import com.ticketpass.model.dto.EventStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;

    // --- Event Tab ---
    private JTable eventTable;
    private DefaultTableModel eventTableModel;
    private JButton btnApproveEvent;
    private JButton btnCancelEvent;
    private JButton btnDeleteEvent;
    private JButton btnViewEventStats;
    private JTextArea eventReportArea;

    // --- User Tab ---
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JButton btnLockUser;
    private JButton btnUnlockUser;
    private JButton btnRefreshUsers;

    public AdminDashboardWindow(TicketPass ticketPass, User currentUser) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;

        setTitle("TicketPass - Admin Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        layoutComponents();
        attachListeners();
        loadAllEvents();
        loadAllUsers();
    }

    private void initComponents() {
        // --- Event Table ---
        String[] eventColumns = {"Event ID", "Name", "Category", "Date", "Venue", "Capacity", "Price", "Status"};
        eventTableModel = new DefaultTableModel(eventColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);

        btnApproveEvent   = new JButton("Approve Event");
        btnCancelEvent    = new JButton("Cancel Event");
        btnDeleteEvent    = new JButton("Delete Event");
        btnViewEventStats = new JButton("View Stats");

        eventReportArea = new JTextArea(8, 30);
        eventReportArea.setEditable(false);
        eventReportArea.setBorder(BorderFactory.createTitledBorder("Event Statistics"));

        // --- User Table ---
        String[] userColumns = {"User ID", "Username", "Email", "Role", "Locked", "Failed Attempts"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);

        btnLockUser     = new JButton("Lock User");
        btnUnlockUser   = new JButton("Unlock User");
        btnRefreshUsers = new JButton("Refresh");
    }

    private void layoutComponents() {
        // ---- Header ----
        JLabel lblHeader = new JLabel("  Welcome, Admin: " + currentUser.getUsername(), SwingConstants.LEFT);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 14));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblHeader, BorderLayout.NORTH);

        // ---- Tabbed Pane ----
        JTabbedPane tabbedPane = new JTabbedPane();

        // -- Events Tab --
        JPanel eventPanel = new JPanel(new BorderLayout(8, 8));

        JPanel eventButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        eventButtonPanel.add(btnApproveEvent);
        eventButtonPanel.add(btnCancelEvent);
        eventButtonPanel.add(btnDeleteEvent);
        eventButtonPanel.add(btnViewEventStats);
        eventPanel.add(eventButtonPanel, BorderLayout.NORTH);

        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventScrollPane.setBorder(BorderFactory.createTitledBorder("All Events"));
        eventPanel.add(eventScrollPane, BorderLayout.CENTER);

        JScrollPane reportScrollPane = new JScrollPane(eventReportArea);
        eventPanel.add(reportScrollPane, BorderLayout.SOUTH);

        tabbedPane.addTab("Events", eventPanel);

        // -- Users Tab --
        JPanel userPanel = new JPanel(new BorderLayout(8, 8));

        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userButtonPanel.add(btnLockUser);
        userButtonPanel.add(btnUnlockUser);
        userButtonPanel.add(btnRefreshUsers);
        userPanel.add(userButtonPanel, BorderLayout.NORTH);

        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("All Users"));
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Users", userPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void attachListeners() {

        // Approve Event (SRS-TP-004)
        btnApproveEvent.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Approve this event?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.approveEvent(currentUser, eventId);
                    JOptionPane.showMessageDialog(this, "Event Approved!");
                    loadAllEvents();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Cancel Event (SRS-TP-005)
        btnCancelEvent.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to cancel this event?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.cancelEvent(currentUser, eventId);
                    JOptionPane.showMessageDialog(this, "Event Cancelled!");
                    loadAllEvents();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete Event (SRS-TP-005)
        btnDeleteEvent.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Permanently delete this event? This cannot be undone.", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.deleteEvent(currentUser, eventId);
                    JOptionPane.showMessageDialog(this, "Event Deleted.");
                    loadAllEvents();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // View Event Stats (SRS-TP-008)
        btnViewEventStats.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                EventStats stats = ticketPass.viewEventStatistics(currentUser, eventId);
                if (stats != null) {
                    eventReportArea.setText(
                            "--- Sales Report for Event ID: " + eventId + " ---\n" +
                                    "Venue Capacity:   " + stats.getVenueCapacity() + "\n" +
                                    "Seats Purchased:  " + stats.getSeatsPurchased() + "\n" +
                                    "Occupancy Rate:   " + stats.getOccupancyRate() + "%\n" +
                                    "Expected Revenue: $" + stats.getExpectedRevenue() + "\n" +
                                    "-----------------------------------");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not retrieve stats.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event to view its statistics.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Lock User (SRS-TP-010)
        btnLockUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId   = (int)    userTableModel.getValueAt(selectedRow, 0);
                String uname = (String) userTableModel.getValueAt(selectedRow, 1);
                int confirm  = JOptionPane.showConfirmDialog(this,
                        "Lock account of user: " + uname + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.lockUserAccount(currentUser, userId);
                    JOptionPane.showMessageDialog(this, "User account locked.");
                    loadAllUsers();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Unlock User (SRS-TP-010)
        btnUnlockUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId   = (int)    userTableModel.getValueAt(selectedRow, 0);
                String uname = (String) userTableModel.getValueAt(selectedRow, 1);
                int confirm  = JOptionPane.showConfirmDialog(this,
                        "Unlock account of user: " + uname + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ticketPass.unlockUserAccount(currentUser, userId);
                    JOptionPane.showMessageDialog(this, "User account unlocked.");
                    loadAllUsers();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Refresh Users
        btnRefreshUsers.addActionListener(e -> loadAllUsers());
    }

    private void loadAllEvents() {
        eventTableModel.setRowCount(0);
        List<Event> allEvents = ticketPass.getAllEvents(currentUser);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
        for (Event event : allEvents) {
            eventTableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getName(),
                    event.getCategory(),
                    event.getEventDate().format(formatter),
                    event.getVenueName(),
                    event.getVenueCapacity(),
                    String.format("$%.2f", event.getPrice()),
                    event.getStatus()
            });
        }
    }

    private void loadAllUsers() {
        userTableModel.setRowCount(0);
        List<User> allUsers = ticketPass.getUserList(currentUser);
        for (User user : allUsers) {
            userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.isLocked() ? "Yes" : "No",
                    user.getFailedAttempts()
            });
        }
    }
}
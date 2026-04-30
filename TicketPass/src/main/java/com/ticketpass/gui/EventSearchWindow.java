package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Event;
import com.ticketpass.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class EventSearchWindow extends JFrame {
    private TicketPass ticketPass;
    private User user;
    private DefaultTableModel tableModel;
    private JTable resultTable;

    public EventSearchWindow(TicketPass ticketPass, User user) {
        this.ticketPass = ticketPass;
        this.user = user;

        setTitle("TicketPass - Advanced Search");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        JLabel lblTitle = new JLabel("Advanced Event Search");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel searchFormPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        JComboBox<String> cbCategory = new JComboBox<>(new String[]{"All Categories", "Football", "Music", "Theater", "Comedy"});
        JTextField txtLocation = new JTextField(15);

        row1.add(new JLabel("Category:"));
        row1.add(cbCategory);
        row1.add(new JLabel("Location:"));
        row1.add(txtLocation);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        JTextField txtPrice = new JTextField(10);
        JTextField txtDate = new JTextField(10);
        txtDate.setToolTipText("YYYY-MM-DD");

        JButton btnSearch = new JButton("Search Events");
        btnSearch.setBackground(new Color(41, 128, 185));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setFocusPainted(false);

        row2.add(new JLabel("Max Price ($):"));
        row2.add(txtPrice);
        row2.add(new JLabel("Date (YYYY-MM-DD):"));
        row2.add(txtDate);
        row2.add(Box.createHorizontalStrut(20));
        row2.add(btnSearch);

        searchFormPanel.add(row1);
        searchFormPanel.add(row2);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(searchFormPanel, BorderLayout.CENTER);

        String[] cols = {"Event ID", "Name", "Category", "Date", "Venue", "Price", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(30);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        resultTable.getColumnModel().getColumn(0).setMinWidth(0);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(0);
        resultTable.getColumnModel().getColumn(0).setWidth(0);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnBack = new JButton("Back to Dashboard");
        JButton btnViewDetails = new JButton("View Event Details");

        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setFocusPainted(false);

        btnViewDetails.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnViewDetails.setBackground(new Color(46, 204, 113));
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setFocusPainted(false);

        bottomPanel.add(btnBack);
        bottomPanel.add(btnViewDetails);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        getRootPane().setDefaultButton(btnSearch);

        btnSearch.addActionListener(e -> {
            String category = cbCategory.getSelectedItem().toString();
            if (category.equals("All Categories")) category = null;

            String location = txtLocation.getText().trim().isEmpty() ? null : txtLocation.getText().trim();

            double price = Double.MAX_VALUE;
            if (!txtPrice.getText().trim().isEmpty()) {
                try {
                    price = Double.parseDouble(txtPrice.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for Max Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Date searchDate = null;
            if (!txtDate.getText().trim().isEmpty()) {
                try {
                    searchDate = new SimpleDateFormat("yyyy-MM-dd").parse(txtDate.getText().trim());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter the date in YYYY-MM-DD format.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            loadResults(category, searchDate, location, price, null);
        });

        btnBack.addActionListener(e -> {
            new CustomerDashboardWindow(ticketPass, user).setVisible(true);
            dispose();
        });

        btnViewDetails.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventId = (int) tableModel.getValueAt(selectedRow, 0);
                new EventDetailsWindow(ticketPass, user, eventId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event from the search results.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSearch.doClick();
    }

    private void loadResults(String category, Date date, String location, double price, String artist) {
        tableModel.setRowCount(0);

        List<Event> dbResults = ticketPass.searchEvents(category, date, null, price, artist);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");

        for (Event event : dbResults) {

            if (location != null && !location.trim().isEmpty()) {
                String searchTarget = location.toLowerCase().trim();

                String venueName = event.getVenueName() != null ? event.getVenueName().toLowerCase() : "";
                String address = event.getAddress() != null ? event.getAddress().toLowerCase() : "";

                if (!venueName.contains(searchTarget) && !address.contains(searchTarget)) {
                    continue;
                }
            }

            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getName(),
                    event.getCategory(),
                    event.getEventDate().format(formatter),
                    event.getVenueName(),
                    String.format("$%.2f", event.getPrice()),
                    event.getStatus().toString()
            });
        }
    }
}
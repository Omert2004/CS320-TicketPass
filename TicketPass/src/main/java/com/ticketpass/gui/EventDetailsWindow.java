package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.Event;
import com.ticketpass.model.User;
import com.ticketpass.model.dto.EventDetails;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class EventDetailsWindow extends JFrame {
    private TicketPass ticketPass;
    private User user;
    private int eventId;

    public EventDetailsWindow(TicketPass ticketPass, User user, int eventId) {
        this.ticketPass = ticketPass;
        this.user = user;
        this.eventId = eventId;

        setTitle("TicketPass - Event Details");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Event event = ticketPass.getEventDetails(eventId).getEvent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm");

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        JLabel lblEventName = new JLabel(event.getName(), SwingConstants.CENTER);
        lblEventName.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblCategory = new JLabel(event.getCategory(), SwingConstants.CENTER);
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCategory.setForeground(new Color(41, 128, 185));

        headerPanel.add(lblEventName);
        headerPanel.add(lblCategory);

        JPanel detailsPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        detailsPanel.add(createDetailRow("Date:", event.getEventDate().format(formatter)));
        detailsPanel.add(createDetailRow("Venue:", event.getVenueName()));
        detailsPanel.add(createDetailRow("Address:", event.getAddress()));
        detailsPanel.add(createDetailRow("Capacity:", String.valueOf(event.getVenueCapacity())));
        detailsPanel.add(createDetailRow("Price:", String.format("$%.2f", event.getPrice())));
        detailsPanel.add(createDetailRow("Status:", event.getStatus().toString()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton btnBack = new JButton("Back");
        JButton btnSelectSeats = new JButton("Select Seats");

        btnBack.setPreferredSize(new Dimension(130, 38));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setFocusPainted(false);

        btnSelectSeats.setPreferredSize(new Dimension(150, 38));
        btnSelectSeats.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSelectSeats.setBackground(new Color(46, 204, 113));
        btnSelectSeats.setForeground(Color.WHITE);
        btnSelectSeats.setFocusPainted(false);

        if (!event.getStatus().toString().equals("ACTIVE")) {
            btnSelectSeats.setEnabled(false);
            btnSelectSeats.setBackground(Color.GRAY);
        }

        buttonPanel.add(btnBack);
        buttonPanel.add(btnSelectSeats);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnBack.addActionListener(e -> {
            new CustomerDashboardWindow(ticketPass, user).setVisible(true);
            dispose();
        });

        btnSelectSeats.addActionListener(e -> {
            new SeatingChartWindow(ticketPass, user, eventId).setVisible(true);
            dispose();
        });
    }

    private JPanel createDetailRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setPreferredSize(new Dimension(100, 20));

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }
}
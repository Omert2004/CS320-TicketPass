package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TicketSuccessWindow extends JFrame {

    private TicketPass ticketPass;
    private User currentUser;
    private int ticketId;

    public TicketSuccessWindow(TicketPass ticketPass, User currentUser, int ticketId) {
        this.ticketPass = ticketPass;
        this.currentUser = currentUser;
        this.ticketId = ticketId;

        setTitle("TicketPass - Purchase Successful!");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblIcon = new JLabel("✔", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 50));
        lblIcon.setForeground(new Color(40, 167, 69));

        JLabel lblTitle = new JLabel("Payment Confirmed!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        headerPanel.add(lblIcon);
        headerPanel.add(lblTitle);

        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        Image qrImage = ticketPass.viewTicketQRCode(currentUser.getUserId(), ticketId);

        JLabel lblQRCode;
        if (qrImage != null) {
            lblQRCode = new JLabel(new ImageIcon(qrImage));
        } else {
            lblQRCode = new JLabel("QR Code Generation Failed", SwingConstants.CENTER);
            lblQRCode.setForeground(Color.RED);
        }

        JLabel lblInstructions = new JLabel("Present this QR code at the venue entrance.", SwingConstants.CENTER);
        lblInstructions.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInstructions.setBorder(new EmptyBorder(10, 0, 10, 0));

        qrPanel.add(lblQRCode, BorderLayout.CENTER);
        qrPanel.add(lblInstructions, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnDownload = new JButton("Download PDF Ticket");
        btnDownload.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDownload.setPreferredSize(new Dimension(0, 45));
        btnDownload.setBackground(new Color(0, 123, 255));
        btnDownload.setForeground(Color.WHITE);

        JButton btnDashboard = new JButton("Back to Dashboard");
        btnDashboard.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        buttonPanel.add(btnDownload);
        buttonPanel.add(btnDashboard);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(qrPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnDownload.addActionListener(e -> handlePdfDownload());

        btnDashboard.addActionListener(e -> {
            new CustomerDashboardWindow(ticketPass, currentUser).setVisible(true);
            dispose();
        });
    }

    private void handlePdfDownload() {
        File generatedPdf = ticketPass.downloadTicketPDF(currentUser.getUserId(), ticketId);

        if (generatedPdf == null || !generatedPdf.exists()) {
            JOptionPane.showMessageDialog(this, "Error generating PDF. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Your Ticket");
        fileChooser.setSelectedFile(new File("Ticket_" + ticketId + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Files.copy(generatedPdf.toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Ticket saved successfully to: " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
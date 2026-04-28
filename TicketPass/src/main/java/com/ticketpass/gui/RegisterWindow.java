package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import javax.swing.*;
import java.awt.*;

public class RegisterWindow extends JFrame {
    private TicketPass ticketPass;

    public RegisterWindow(TicketPass ticketPass) {
        this.ticketPass = ticketPass;
        setTitle("TicketPass - Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField txtUser = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"CUSTOMER", "ORGANIZER"});

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUser);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPass);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton btnRegister = new JButton("Create Account");
        JButton btnBack = new JButton("Back");

        btnRegister.setPreferredSize(new Dimension(130, 30));
        btnBack.setPreferredSize(new Dimension(100, 30));

        buttonPanel.add(btnBack);
        buttonPanel.add(btnRegister);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText();
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());
            String role = cbRole.getSelectedItem().toString();

            boolean success = ticketPass.register(user, email, pass, role);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginWindow(ticketPass).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Username or Email may exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> {
            new LoginWindow(ticketPass).setVisible(true);
            dispose();
        });
    }
}
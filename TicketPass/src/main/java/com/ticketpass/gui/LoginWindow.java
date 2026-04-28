package com.ticketpass.gui;

import com.ticketpass.controller.TicketPass;
import com.ticketpass.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private TicketPass ticketPass;

    public LoginWindow(TicketPass ticketPass) {
        this.ticketPass = ticketPass;
        setTitle("TicketPass - Welcome");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel welcomePanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JLabel lblLogo = new JLabel(createIcon("/logo.png", 70, 70), SwingConstants.CENTER);

        JLabel lblWelcome = new JLabel("Welcome to TicketPass", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JLabel lblFootball = new JLabel(createIcon("/tsl.png", 50, 50));
        JLabel lblMusic = new JLabel(createIcon("/keman.png", 36, 36));
        JLabel lblSubtitleText = new JLabel("Football, music and everything in between");
        lblSubtitleText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitleText.setForeground(Color.DARK_GRAY);

        subtitlePanel.add(lblFootball);
        subtitlePanel.add(lblSubtitleText);
        subtitlePanel.add(lblMusic);

        welcomePanel.add(lblLogo);
        welcomePanel.add(lblWelcome);
        welcomePanel.add(subtitlePanel);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));

        JLabel lblUsername = new JLabel("Username:", createIcon("/user.png", 20, 20), SwingConstants.LEFT);
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblPassword = new JLabel("Password:", createIcon("/lock.png", 20, 20), SwingConstants.LEFT);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(lblUsername);
        formPanel.add(txtUser);
        formPanel.add(lblPassword);
        formPanel.add(txtPass);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnRegister.setPreferredSize(new Dimension(120, 35));
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnLogin);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> {
            String username = txtUser.getText();
            String password = new String(txtPass.getPassword());
            User user = ticketPass.login(username, password);

            if (user != null) {
                //new DashboardWindow(ticketPass, user).setVisible(true);
                //dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or account locked.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> {
            new RegisterWindow(ticketPass).setVisible(true);
            dispose();
        });
    }

    private ImageIcon createIcon(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return new ImageIcon();
    }
}
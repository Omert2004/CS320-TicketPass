package com.ticketpass.controller;

import com.ticketpass.util.DatabaseManager;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class PaymentAndTicketingService {

    public int processPayment(int userId, String lastFourDigits, String paymentToken) {
        int transactionId = -1;
        String query = "{CALL sp_processPayment(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, lastFourDigits);
            stmt.setString(3, paymentToken);
            stmt.setString(4, "SUCCESS");
            stmt.registerOutParameter(5, Types.INTEGER);

            stmt.execute();
            transactionId = stmt.getInt(5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionId;
    }

    public int generateTicketRecord(int userId, int eventId, int seatId, int transactionId) {
        int ticketId = -1;
        String qrCodeData = UUID.randomUUID().toString();
        String query = "{CALL sp_generateTicket(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, seatId);
            stmt.setInt(4, transactionId);
            stmt.setString(5, qrCodeData);
            stmt.registerOutParameter(6, Types.INTEGER);

            stmt.execute();
            ticketId = stmt.getInt(6);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketId;
    }

    public boolean retryPayment(int transactionId, String newPaymentToken) {
        return true;
    }

    public void holdSeatLockOnFail(int transactionId) {
    }

    public File generatePDFTicket(int ticketId) {
        return new File("ticket_" + ticketId + ".pdf");
    }

    public Image generateQRCode(int ticketId) {
        return new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    }
}
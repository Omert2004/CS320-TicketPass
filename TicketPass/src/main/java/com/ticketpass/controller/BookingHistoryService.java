package com.ticketpass.controller;

import com.ticketpass.model.dto.Booking;
import com.ticketpass.util.DatabaseManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryService {

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "{CALL sp_getTicketsByUser(?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, "DATE_DESC");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setTicketId(rs.getInt("ticketId"));

                    if (rs.getTimestamp("purchaseTime") != null) {
                        booking.setPurchaseTime(rs.getTimestamp("purchaseTime").toLocalDateTime());
                    }

                    booking.setQrCode(rs.getString("qrCode"));
                    booking.setPdfPath(rs.getString("pdfPath"));
                    booking.setEventName(rs.getString("eventName"));

                    if (rs.getTimestamp("eventDate") != null) {
                        booking.setEventDate(rs.getTimestamp("eventDate").toLocalDateTime());
                    }

                    booking.setAddress(rs.getString("address"));
                    booking.setVenueName(rs.getString("venueName"));
                    booking.setRowLabel(rs.getString("rowLabel"));
                    booking.setSeatNumber(rs.getInt("seatNumber"));

                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}
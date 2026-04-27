package com.ticketpass.controller;

import com.ticketpass.model.Event;
import com.ticketpass.util.DatabaseManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AdminManagementService {

    public void createEvent(int adminId, Event eventData) {
        String query = "{CALL sp_createEvent(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setString(2, eventData.getName());
            stmt.setString(3, eventData.getCategory());
            stmt.setTimestamp(4, Timestamp.valueOf(eventData.getEventDate()));
            stmt.setString(5, eventData.getAddress());
            stmt.setString(6, eventData.getVenueName());
            stmt.setInt(7, eventData.getVenueCapacity());
            stmt.setDouble(8, eventData.getPrice());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(int adminId, int eventId, Event eventData) {
        String query = "{CALL sp_editEvent(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.setString(3, eventData.getName());
            stmt.setString(4, eventData.getCategory());
            stmt.setTimestamp(5, Timestamp.valueOf(eventData.getEventDate()));
            stmt.setString(6, eventData.getAddress());
            stmt.setDouble(7, eventData.getPrice());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelEvent(int adminId, int eventId) {
        String query = "{CALL sp_cancelEvent(?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSeatAvailability(int adminId, int seatId, String status) {
        String query = "{CALL sp_updateSeatAvailability(?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, seatId);
            stmt.setString(3, status);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package com.ticketpass.controller;

import com.ticketpass.model.Event;
import com.ticketpass.model.Role;
import com.ticketpass.model.User;
import com.ticketpass.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String query = "{CALL sp_editEvent(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.setString(3, eventData.getName());
            stmt.setString(4, eventData.getCategory());
            stmt.setTimestamp(5, Timestamp.valueOf(eventData.getEventDate()));
            stmt.setString(6, eventData.getAddress());
            stmt.setDouble(7, eventData.getPrice());
            stmt.setInt(8, eventData.getVenueCapacity());
            stmt.setString(9, eventData.getStatus().toString());

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

    public void approveEvent(int adminId, int eventId) {
        String query = "{CALL sp_approveEvent(?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int adminId, int eventId) {
        String query = "{CALL sp_deleteEvent(?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getOrganizerEvents(int organizerId) {
        List<Event> events = new ArrayList<>();
        String query = "{CALL sp_getOrganizerEvents(?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, organizerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getInt("eventId"));
                    event.setOrganizerId(rs.getInt("organizerId"));
                    event.setName(rs.getString("name"));
                    event.setCategory(rs.getString("category"));
                    if (rs.getTimestamp("eventDate") != null) {
                        event.setEventDate(rs.getTimestamp("eventDate").toLocalDateTime());
                    }
                    event.setAddress(rs.getString("address"));
                    event.setVenueName(rs.getString("venueName"));
                    event.setVenueCapacity(rs.getInt("venueCapacity"));
                    event.setPrice(rs.getDouble("price"));
                    event.setStatus(com.ticketpass.model.EventStatus.valueOf(rs.getString("status").toUpperCase()));

                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "{CALL sp_getUserList()}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Role userRole = Role.valueOf(rs.getString("role").toUpperCase());
                User user = new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("email"),
                        null,
                        userRole,
                        rs.getBoolean("isLocked"),
                        rs.getInt("failedAttempts"),
                        null // CreatedAt can be mapped if added to User model
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
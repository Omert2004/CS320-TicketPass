package com.ticketpass.controller;

import com.ticketpass.model.Event;
import com.ticketpass.model.EventStatus;
import com.ticketpass.model.Role;
import com.ticketpass.model.User;
import com.ticketpass.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminManagementService {

    public int createEvent(int adminId, Event eventData) {
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

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("newEventId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void generateSeats(int adminId, int eventId, int rowCount, int seatsPerRow) {
        String query = "{CALL sp_generateSeats(?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, rowCount);
            stmt.setInt(3, seatsPerRow);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(int adminId, int eventId, Event eventData) {
        String query = "{CALL sp_editEvent(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);
            stmt.setString(3, eventData.getName());
            stmt.setString(4, eventData.getCategory());
            stmt.setTimestamp(5, Timestamp.valueOf(eventData.getEventDate()));
            stmt.setString(6, eventData.getAddress());
            stmt.setString(7, eventData.getVenueName());
            stmt.setInt(8, eventData.getVenueCapacity());
            stmt.setDouble(9, eventData.getPrice());
            stmt.setString(10, eventData.getStatus().toString());

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

    public void generateSeatingChart(int eventId, int rows, int columns) {
        String insertSeatsQuery = "INSERT INTO seats (eventId, rowLabel, seatNumber, status) VALUES (?, ?, ?, 'AVAILABLE')";
        String updateCapacityQuery = "UPDATE events SET venueCapacity = ? WHERE eventId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSeatsQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateCapacityQuery)) {

            for (int r = 0; r < rows; r++) {
                String rowLabel = String.valueOf((char) ('A' + r));
                for (int c = 1; c <= columns; c++) {
                    insertStmt.setInt(1, eventId);
                    insertStmt.setString(2, rowLabel);
                    insertStmt.setInt(3, c);
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();

            int totalCapacity = rows * columns;
            updateStmt.setInt(1, totalCapacity);
            updateStmt.setInt(2, eventId);
            updateStmt.executeUpdate();

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

    public List<Event> getOrganizerEvents(int organizerId) {
        List<Event> events = new ArrayList<>();
        String query = "{CALL sp_getOrganizerEvents(?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("eventId"));
                event.setOrganizerId(rs.getInt("organizerId"));
                event.setName(rs.getString("name"));
                event.setCategory(rs.getString("category"));
                event.setEventDate(rs.getTimestamp("eventDate").toLocalDateTime());
                event.setAddress(rs.getString("address"));
                event.setVenueName(rs.getString("venueName"));
                event.setVenueCapacity(rs.getInt("venueCapacity"));
                event.setPrice(rs.getDouble("price"));
                event.setStatus(EventStatus.valueOf(rs.getString("status").toUpperCase()));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }


    // Returns all events regardless of status — used by AdminDashboardWindow
    public List<Event> getAllEvents(int adminId) {
        List<Event> events = new ArrayList<>();
        String query = "{CALL sp_getAllEvents(?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("eventId"));
                event.setOrganizerId(rs.getInt("organizerId"));
                event.setName(rs.getString("name"));
                event.setCategory(rs.getString("category"));
                event.setEventDate(rs.getTimestamp("eventDate").toLocalDateTime());
                event.setAddress(rs.getString("address"));
                event.setVenueName(rs.getString("venueName"));
                event.setVenueCapacity(rs.getInt("venueCapacity"));
                event.setPrice(rs.getDouble("price"));
                event.setStatus(EventStatus.valueOf(rs.getString("status").toUpperCase()));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Returns all users — used by AdminDashboardWindow
    public List<User> getUserList(int adminId) {
        return getAllUsers();
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
                        null
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lock a user account (SRS-TP-010)
    public void lockUserAccount(int adminId, int targetUserId) {
        String query = "{CALL sp_lockAccount(?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, targetUserId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Unlock a user account (SRS-TP-010)
    public void unlockUserAccount(int adminId, int targetUserId) {
        String query = "{CALL sp_resetFailedAttempts(?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, targetUserId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
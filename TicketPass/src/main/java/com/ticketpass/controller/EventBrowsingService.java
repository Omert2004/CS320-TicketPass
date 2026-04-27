package com.ticketpass.controller;

import com.ticketpass.model.Event;
import com.ticketpass.model.EventStatus;
import com.ticketpass.model.dto.EventDetails;
import com.ticketpass.util.DatabaseManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventBrowsingService {

    public List<Event> getUpcomingEvents() {
        List<Event> events = new ArrayList<>();
        String query = "{CALL sp_getUpcomingEvents()}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> searchEvents(String category, Date date, String location, double price, String artist) {
        List<Event> events = new ArrayList<>();
        String query = "{CALL sp_getEventsByFilter(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setString(1, category);
            stmt.setDate(2, date != null ? new java.sql.Date(date.getTime()) : null);
            stmt.setString(3, location);
            stmt.setDouble(4, price);
            stmt.setString(5, artist);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public EventDetails getEventDetails(int eventId) {
        EventDetails details = null;
        String query = "{CALL sp_getEventDetails(?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Event event = mapRowToEvent(rs);
                    int availableSeats = rs.getInt("availableSeats");
                    details = new EventDetails(event, availableSeats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
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
        event.setStatus(EventStatus.valueOf(rs.getString("status").toUpperCase()));
        if (rs.getTimestamp("createdAt") != null) {
            event.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        }
        return event;
    }
}
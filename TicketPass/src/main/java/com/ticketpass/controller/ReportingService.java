package com.ticketpass.controller;

import com.ticketpass.model.dto.EventStats;
import com.ticketpass.model.dto.Report;
import com.ticketpass.model.dto.ReportItem;
import com.ticketpass.util.DatabaseManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportingService {

    public Report generateSalesReport(int adminId) {
        List<ReportItem> items = new ArrayList<>();
        String query = "{CALL sp_getSalesReport(?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportItem item = new ReportItem();
                    item.setEventId(rs.getInt("eventId"));
                    item.setEventName(rs.getString("eventName"));

                    if (rs.getTimestamp("eventDate") != null) {
                        item.setEventDate(rs.getTimestamp("eventDate").toLocalDateTime());
                    }

                    item.setPrice(rs.getDouble("price"));
                    item.setTicketsSold(rs.getInt("ticketsSold"));
                    item.setOccupancyRate(rs.getDouble("occupancyRate"));
                    item.setTotalRevenue(rs.getDouble("totalRevenue"));

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Report(items);
    }

    public EventStats getEventStatistics(int adminId, int eventId) {
        EventStats stats = null;
        String query = "{CALL sp_getEventStats(?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, adminId);
            stmt.setInt(2, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats = new EventStats();
                    stats.setEventId(rs.getInt("eventId"));
                    stats.setVenueCapacity(rs.getInt("venueCapacity"));
                    stats.setSeatsPurchased(rs.getInt("seatsPurchased"));
                    stats.setOccupancyRate(rs.getDouble("occupancyRate"));
                    stats.setExpectedRevenue(rs.getDouble("expectedRevenue"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
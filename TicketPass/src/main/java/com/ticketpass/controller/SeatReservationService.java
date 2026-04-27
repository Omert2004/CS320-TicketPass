package com.ticketpass.controller;

import com.ticketpass.model.Seat;
import com.ticketpass.model.SeatStatus;
import com.ticketpass.model.dto.SeatingChart;
import com.ticketpass.util.DatabaseManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SeatReservationService {

    public SeatingChart getSeatingChart(int eventId) {
        List<Seat> seats = new ArrayList<>();
        String query = "{CALL sp_getSeatsByEvent(?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("seatId"));
                    seat.setEventId(rs.getInt("eventId"));
                    seat.setRowLabel(rs.getString("rowLabel"));
                    seat.setSeatNumber(rs.getInt("seatNumber"));
                    seat.setStatus(SeatStatus.valueOf(rs.getString("status").toUpperCase()));
                    seat.setLockedBy(rs.getInt("lockedBy"));
                    if (rs.getTimestamp("lockExpires") != null) {
                        seat.setLockExpires(rs.getTimestamp("lockExpires").toLocalDateTime());
                    }
                    seats.add(seat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SeatingChart(eventId, seats);
    }

    public boolean lockSeat(int seatId, int userId) {
        boolean success = false;
        String query = "{CALL sp_lockSeat(?, ?, ?)}";

        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, seatId);
            stmt.setInt(2, userId);
            stmt.registerOutParameter(3, Types.BOOLEAN);

            stmt.execute();
            success = stmt.getBoolean(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void releaseSeatLock(int seatId) {
        String query = "{CALL sp_releaseSeat(?)}";
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, seatId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
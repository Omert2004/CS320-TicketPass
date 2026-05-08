package com.ticketpass.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.ticketpass.model.dto.SeatingChart;

//Covers requirements: T-SRS-TP-002.1 , 002.2, 010.1
public class SeatReservationServiceTest {

    private SeatReservationService seatService;

    @BeforeEach
    public void setup() {
        seatService = new SeatReservationService();
    }

    // T-SRS-TP-002.1 - Unit Tests
    @Test
    @DisplayName("T-SRS-TP-002.1: Venue-Specific Seating Charts")
    public void getSeatingChart_ValidEventId_ReturnsChartWithSeats() {
        int validEventId = 1;

        SeatingChart chart = seatService.getSeatingChart(validEventId);

        assertNotNull(chart, "Seating chart should not be null.");
        assertEquals(validEventId, chart.getEventId(), "Chart should match the requested event ID.");
        assertFalse(chart.getSeats().isEmpty(), "Seating chart should contain seats for a valid event.");
    }

    @Test
    @DisplayName("T-SRS-TP-002.1: Venue-Specific Seating Charts - Invalid Event")
    public void getSeatingChart_InvalidEventId_ReturnsEmptyChart() {
        int invalidEventId = 999; // Assuming event 999 does not exist

        SeatingChart chart = seatService.getSeatingChart(invalidEventId);

        assertNotNull(chart, "Seating chart should not be null even for invalid events.");
        assertTrue(chart.getSeats().isEmpty(), "Seating chart should be empty for an invalid event.");
    }

    // T-SRS-TP-002.2 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-002.2: Concurrent Seat Selection - Happy Path")
    public void lockSeat_SeatIsAvailable_ReturnsTrue() {
        int targetSeatId = 1; // From seed data: Event 1, Row A, Seat 1
        int userId = 3;       // From seed data: customer1

        seatService.releaseSeatLock(targetSeatId);
        boolean isLocked = seatService.lockSeat(targetSeatId, userId);

        assertTrue(isLocked, "The system should return true when successfully locking an available seat.");
        seatService.releaseSeatLock(targetSeatId);
    }

    @Test
    @DisplayName("T-SRS-TP-002.2: Concurrent Seat Selection - Sad Path")
    public void lockSeat_SeatAlreadyLockedByAnotherUser_ReturnsFalse() {
        int targetSeatId = 2; // Event 1, Row A, Seat 2
        int userA_Id = 3;     // customer1
        int userB_Id = 4;     // customer2

        seatService.releaseSeatLock(targetSeatId);
        boolean userASuccess = seatService.lockSeat(targetSeatId, userA_Id);        //First A locks
        boolean userBSuccess = seatService.lockSeat(targetSeatId, userB_Id);        //Then B tries to lock

        assertTrue(userASuccess, "User A should successfully lock the seat.");
        assertFalse(userBSuccess, "User B should be blocked by the database and receive false.");

        seatService.releaseSeatLock(targetSeatId);
    }

    // T-SRS-TP-010.1 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-010.1: Payment Failure Grace Period Lock")
    public void getSeatingChart_SeatLocked_DisplaysLockExpirationFutureTime() {
        int targetSeatId = 3; // Event 1, Row A, Seat 3
        int eventId = 1;
        int userId = 3;

        seatService.releaseSeatLock(targetSeatId);
        seatService.lockSeat(targetSeatId, userId);

        SeatingChart chart = seatService.getSeatingChart(eventId);

        var lockedSeat = chart.getSeats().stream()
                .filter(seat -> seat.getSeatId() == targetSeatId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seat not found in chart"));

        assertNotNull(lockedSeat.getLockExpires(), "Locked seat must have an expiration timestamp.");
        assertTrue(lockedSeat.getLockExpires().isAfter(java.time.LocalDateTime.now()),
                "The lock expiration time should be set in the future (grace period).");

        seatService.releaseSeatLock(targetSeatId);
    }

    @Test
    @DisplayName("T-SRS-TP-010.1: Lock sets expiration exactly 10 minutes in the future")
    public void lockSeat_NewReservation_SetsExpirationTenMinutesFromNow() {
        int targetSeatId = 3; // Event 1, Row A, Seat 3
        int eventId = 1;
        int userId = 3;

        seatService.releaseSeatLock(targetSeatId);
        seatService.lockSeat(targetSeatId, userId);

        SeatingChart chart = seatService.getSeatingChart(eventId);
        var lockedSeat = chart.getSeats().stream()
                .filter(seat -> seat.getSeatId() == targetSeatId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seat not found"));

        assertNotNull(lockedSeat.getLockExpires(), "Locked seat must have an expiration timestamp.");

        java.time.LocalDateTime expectedTime = java.time.LocalDateTime.now().plusMinutes(10);
        java.time.LocalDateTime actualTime = lockedSeat.getLockExpires();

        assertTrue(actualTime.isAfter(expectedTime.minusSeconds(5)) && actualTime.isBefore(expectedTime.plusSeconds(5)),
                "The lock expiration should be exactly 10 minutes from the time of selection.");

        seatService.releaseSeatLock(targetSeatId);
    }

    @Test
    @DisplayName("T-SRS-TP-010.1: Seat lock release clears expiration and user ID")
    public void releaseSeatLock_SeatWasLocked_ClearsLockDataSuccessfully() {
        int targetSeatId = 4; // Event 1, Row A, Seat 4
        int eventId = 1;
        int userId = 3;

        seatService.releaseSeatLock(targetSeatId);
        seatService.lockSeat(targetSeatId, userId);
        seatService.releaseSeatLock(targetSeatId);

        // Fetch the chart and verify the seat is available again
        SeatingChart chart = seatService.getSeatingChart(eventId);
        var releasedSeat = chart.getSeats().stream()
                .filter(seat -> seat.getSeatId() == targetSeatId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seat not found"));

        assertNull(releasedSeat.getLockExpires(), "Expiration time should be null after lock is released.");
        assertEquals(0, releasedSeat.getLockedBy(), "LockedBy user ID should be reset to 0 (or null) after release.");
        assertEquals("AVAILABLE", releasedSeat.getStatus().toString(), "Seat status should revert to AVAILABLE.");
    }
}
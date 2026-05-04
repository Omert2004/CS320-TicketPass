package com.ticketpass.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class SeatReservationServiceTest {

    private SeatReservationService seatService;

    @BeforeEach
    public void setup() {
        seatService = new SeatReservationService();
    }

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
}
package com.ticketpass.controller;

import java.io.File;

public class SeatReservationService {

    public File getSeatingChart(int eventId) {
        return new File("seating_chart");
    }

    public boolean selectSeat(int seatId, int userId) {
        return true;
    }

    public void lockSeat(int seatId) {
    }

    public void startCheckoutTimer(int transactionId) {
    }

    public void releaseSeatLock(int seatId) {
    }
}
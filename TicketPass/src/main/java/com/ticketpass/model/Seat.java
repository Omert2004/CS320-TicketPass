package com.ticketpass.model;

import java.time.LocalDateTime;

public class Seat {
    private int seatId;
    private int eventId;
    private String rowLabel;
    private int seatNumber;
    private SeatStatus status;
    private int lockedBy; // userId of the person who locked it
    private LocalDateTime lockExpires;

    public Seat() {}

    public Seat(int seatId, int eventId, String rowLabel, int seatNumber, SeatStatus status, int lockedBy, LocalDateTime lockExpires) {
        this.seatId = seatId;
        this.eventId = eventId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.status = status;
        this.lockedBy = lockedBy;
        this.lockExpires = lockExpires;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public int getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(int lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getLockExpires() {
        return lockExpires;
    }

    public void setLockExpires(LocalDateTime lockExpires) {
        this.lockExpires = lockExpires;
    }
}

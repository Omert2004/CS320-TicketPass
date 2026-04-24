package com.ticketpass.model.dto;

import com.ticketpass.model.Seat;
import java.util.List;

public class SeatingChart {
    private int eventId;
    private List<Seat> seats;

    public SeatingChart() {}

    public SeatingChart(int eventId, List<Seat> seats) {
        this.eventId = eventId;
        this.seats = seats;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}
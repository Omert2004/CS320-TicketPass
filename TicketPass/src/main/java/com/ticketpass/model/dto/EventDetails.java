package com.ticketpass.model.dto;

import com.ticketpass.model.Event;

public class EventDetails {
    private Event event;
    private int availableSeats;

    public EventDetails() {}

    public EventDetails(Event event, int availableSeats) {
        this.event = event;
        this.availableSeats = availableSeats;
    }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
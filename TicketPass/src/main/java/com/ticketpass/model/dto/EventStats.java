package com.ticketpass.model.dto;

public class EventStats {
    private int eventId;
    private int venueCapacity;
    private int seatsPurchased;
    private double occupancyRate;
    private double expectedRevenue;

    public EventStats() {}

    public EventStats(int eventId, int venueCapacity, int seatsPurchased, double occupancyRate, double expectedRevenue) {
        this.eventId = eventId;
        this.venueCapacity = venueCapacity;
        this.seatsPurchased = seatsPurchased;
        this.occupancyRate = occupancyRate;
        this.expectedRevenue = expectedRevenue;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(int venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public int getSeatsPurchased() {
        return seatsPurchased;
    }

    public void setSeatsPurchased(int seatsPurchased) {
        this.seatsPurchased = seatsPurchased;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    public void setExpectedRevenue(double expectedRevenue) {
        this.expectedRevenue = expectedRevenue;
    }
}
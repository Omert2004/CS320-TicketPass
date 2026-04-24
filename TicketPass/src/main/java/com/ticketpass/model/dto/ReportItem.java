package com.ticketpass.model.dto;

import java.time.LocalDateTime;

public class ReportItem {
    private int eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private double price;
    private int ticketsSold;
    private double occupancyRate;
    private double totalRevenue;

    public ReportItem() {}

    public ReportItem(int eventId, String eventName, LocalDateTime eventDate, double price, int ticketsSold, double occupancyRate, double totalRevenue) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.price = price;
        this.ticketsSold = ticketsSold;
        this.occupancyRate = occupancyRate;
        this.totalRevenue = totalRevenue;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
package com.ticketpass.model;

import java.time.LocalDateTime;

public class Event {
    private int eventId;
    private int organizerId;
    private String name;
    private String category;
    private LocalDateTime eventDate;
    private String address;
    private String venueName;
    private int venueCapacity;
    private double price;
    private EventStatus status;
    private LocalDateTime createdAt;

    public Event() {}

    public Event(int eventId, int organizerId, String name, String category, LocalDateTime eventDate, String address, String venueName, int venueCapacity, double price, EventStatus status, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.name = name;
        this.category = category;
        this.eventDate = eventDate;
        this.address = address;
        this.venueName = venueName;
        this.venueCapacity = venueCapacity;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(int venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
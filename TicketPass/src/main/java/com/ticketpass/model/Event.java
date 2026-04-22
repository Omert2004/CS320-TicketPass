package com.ticketpass.model;

import java.time.LocalDateTime;

public class Event {
    private int eventId;
    private int organizerId;
    private String name;
    private String category;
    private LocalDateTime eventDate;
    private String venueName;
    private int venueCapacity;
    private String location;
    private float price;
    private EventStatus status;

    public Event() {}

    public Event(int eventId, int organizerId, String name, String category, LocalDateTime eventDate, String venueName, int venueCapacity, String location, float price, EventStatus status) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.name = name;
        this.category = category;
        this.eventDate = eventDate;
        this.venueName = venueName;
        this.venueCapacity = venueCapacity;
        this.location = location;
        this.price = price;
        this.status = status;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
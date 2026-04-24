package com.ticketpass.model.dto;

import java.time.LocalDateTime;

public class Booking {
    // From Ticket table
    private int ticketId;
    private LocalDateTime purchaseTime;
    private String qrCode;
    private String pdfPath;

    // From Event table
    private String eventName;
    private LocalDateTime eventDate;
    private String address;
    private String venueName;

    // From Seat table
    private String rowLabel;
    private int seatNumber;

    public Booking() {}

    public Booking(int ticketId, LocalDateTime purchaseTime, String qrCode, String pdfPath, String eventName, LocalDateTime eventDate, String address, String venueName, String rowLabel, int seatNumber) {
        this.ticketId = ticketId;
        this.purchaseTime = purchaseTime;
        this.qrCode = qrCode;
        this.pdfPath = pdfPath;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.address = address;
        this.venueName = venueName;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDateTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
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
}
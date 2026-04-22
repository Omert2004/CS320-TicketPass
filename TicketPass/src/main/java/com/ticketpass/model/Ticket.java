package com.ticketpass.model;

import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private int transactionId;
    private int eventId;
    private int seatId;
    private String qrCodeData;
    private String status;
    private LocalDateTime purchaseTime;

    public Ticket(int ticketId, int transactionId, int eventId, int seatId, String qrCodeData, LocalDateTime purchaseTime) {
        this.ticketId = ticketId;
        this.transactionId = transactionId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.qrCodeData = qrCodeData;
        this.status = "VALID";
        this.purchaseTime = purchaseTime;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDateTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }
}

package com.ticketpass.model;

import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private int userId;
    private int eventId;
    private int seatId;
    private int transactionId;
    private LocalDateTime purchaseTime;
    private String qrCode;
    private String pdfPath;

    public Ticket() {}

    public Ticket(int ticketId, int userId, int eventId, int seatId, int transactionId, LocalDateTime purchaseTime, String qrCode, String pdfPath) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.transactionId = transactionId;
        this.purchaseTime = purchaseTime;
        this.qrCode = qrCode;
        this.pdfPath = pdfPath;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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
}

package com.ticketpass.model;

public class Ticket {
    private int ticketId;
    private int bookingId;
    private int eventId;
    private int seatId;
    private String qrCodeData;
    private String status;

    public Ticket(int ticketId, int bookingId, int eventId, int seatId, String qrCodeData) {
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.qrCodeData = qrCodeData;
        this.status = "VALID";
    }

    public int getTicketId() { return ticketId; }
    public int getBookingId() { return bookingId; }
    public int getEventId() { return eventId; }
    public int getSeatId() { return seatId; }
    public String getQrCodeData() { return qrCodeData; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}

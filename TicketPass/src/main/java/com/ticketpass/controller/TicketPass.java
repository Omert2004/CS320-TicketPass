package com.ticketpass.controller;

import com.ticketpass.model.*;
import com.ticketpass.model.dto.*;
import com.ticketpass.util.DatabaseManager;

import java.util.List;
import java.util.Date;
import java.io.File;
import java.awt.Image;

public class TicketPass {

    private EventBrowsingService browsingService;
    private SeatReservationService reservationService;
    private PaymentAndTicketingService paymentService;
    private QRCodeService qrCodeService;
    private PdfTicketService pdfTicketService;
    private AdminManagementService adminService;
    private ReportingService reportingService;
    private BookingHistoryService historyService;


    public TicketPass(EventBrowsingService browsing,
                      SeatReservationService reservation, PaymentAndTicketingService payment,
                      QRCodeService qrService, PdfTicketService pdfService,
                      AdminManagementService admin, ReportingService reporting,
                      BookingHistoryService history) {
        this.browsingService = browsing;
        this.reservationService = reservation;
        this.paymentService = payment;
        this.adminService = admin;
        this.reportingService = reporting;
        this.historyService = history;
        this.qrCodeService = new QRCodeService(history);
        this.pdfTicketService = new PdfTicketService(qrService, history);
    }

    public User login(String username, String password) {
        User user = DatabaseManager.authenticate(username, password);
        if (user != null) {
            if (user.isLocked()) {
                return null;
            }
            DatabaseManager.resetFailedAttempts(user.getUserId());
        }
        return user;
    }

    public void handleFailedLoginAttempt(int userId) {
        DatabaseManager.incrementFailedAttempts(userId);
    }

    public boolean register(String username, String email, String password, String role) {
        return DatabaseManager.registerUser(username, email, password, role);
    }

    public List<Event> getUpcomingEvents() {
        return browsingService.getUpcomingEvents();
    }

    public List<Event> getOrganizerEvents(int organizerId) {
        return adminService.getOrganizerEvents(organizerId);
    }

    public List<Event> searchEvents(String category, Date date, String location, double price, String artist) {
        return browsingService.searchEvents(category, date, location, price, artist);
    }

    public EventDetails getEventDetails(int eventId) {
        return browsingService.getEventDetails(eventId);
    }

    public SeatingChart getSeatingChart(int eventId) {
        return reservationService.getSeatingChart(eventId);
    }

    public boolean selectSeat(int seatId, int userId) {
        return reservationService.lockSeat(seatId, userId);
    }

    public void releaseSeatLock(int seatId) {
        reservationService.releaseSeatLock(seatId);
    }

    public int processPayment(int userId, String lastFourDigits, String paymentToken, String status) {
        return paymentService.processPayment(userId, lastFourDigits, paymentToken, status);
    }

    public int generateTicketFlow(int userId, int eventId, int seatId, int transactionId) {
        return paymentService.generateTicketRecord(userId, eventId, seatId, transactionId);
    }

    public File downloadTicketPDF(int userId, int ticketId) {
        return pdfTicketService.generatePDFTicket(userId, ticketId);
    }

    public Image viewTicketQRCode(int userId, int ticketId) {
        return qrCodeService.generateQRCode(userId, ticketId);
    }

    public List<Booking> viewBookingHistory(int userId) {
        return historyService.getUserBookings(userId);
    }

    public void createNewEvent(User currentUser, Event eventData) {
        if (currentUser != null && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ORGANIZER)) {
            adminService.createEvent(currentUser.getUserId(), eventData);
        }
    }

    public void updateEvent(User currentUser, int eventId, Event eventData) {
        if (currentUser != null && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ORGANIZER)) {
            adminService.updateEvent(currentUser.getUserId(), eventId, eventData);
        }
    }

    public void cancelEvent(User currentUser, int eventId) {
        if (currentUser != null && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ORGANIZER)) {
            adminService.cancelEvent(currentUser.getUserId(), eventId);
        }
    }

    public void updateSeatAvailability(User currentUser, int seatId, String status) {
        if (currentUser != null && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ORGANIZER)) {
            adminService.updateSeatAvailability(currentUser.getUserId(), seatId, status);
        }
    }

    public Report viewSalesReport(User currentUser) {
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            return reportingService.generateSalesReport(currentUser.getUserId());
        }
        return null;
    }

    public EventStats viewEventStatistics(User currentUser, int eventId) {
        if (currentUser != null && (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ORGANIZER)) {
            return reportingService.getEventStatistics(currentUser.getUserId(), eventId);
        }
        return null;
    }
}
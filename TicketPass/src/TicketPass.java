import java.util.List;
import java.util.Date;
import java.io.File;
import java.awt.Image;

public class TicketPass {

    private EventBrowsingService browsingService;
    private SeatReservationService reservationService;
    private PaymentAndTicketingService paymentService;
    private AdminManagementService adminService;
    private ReportingService reportingService;
    private BookingHistoryService historyService;

    public TicketPass(EventBrowsingService browsing,
                      SeatReservationService reservation, PaymentAndTicketingService payment,
                      AdminManagementService admin, ReportingService reporting,
                      BookingHistoryService history) {
        this.browsingService = browsing;
        this.reservationService = reservation;
        this.paymentService = payment;
        this.adminService = admin;
        this.reportingService = reporting;
        this.historyService = history;
    }

    public User login(String username, String password) {
        return DatabaseManager.authenticate(username, password);
    }

    public boolean register(String username, String email, String password, String role, String firstName, String lastName) {
        return DatabaseManager.registerUser(username, email, password, role, firstName, lastName);
    }

    public List<Event> getUpcomingEvents() {
        return browsingService.getUpcomingEvents();
    }

    public List<Event> searchEvents(String category, Date date, String location, float price, String artist) {
        return browsingService.searchEvents(category, date, location, price, artist);
    }

    public EventDetails getEventDetails(int eventId) {
        return browsingService.getEventDetails(eventId);
    }

    public File getSeatingChart(int eventId) {
        return reservationService.getSeatingChart(eventId);
    }

    public boolean selectSeat(int seatId, int userId) {
        reservationService.lockSeat(seatId);
        return reservationService.selectSeat(seatId, userId);
    }

    public boolean processPayment(int userId, String paymentToken, int transactionId) {
        boolean success = paymentService.processPayment(userId, paymentToken);
        if (!success) {
            paymentService.holdSeatLockOnFail(transactionId);
        }
        return success;
    }

    public File generateTicketFlow(int bookingId) {
        Image qrCode = paymentService.generateQRCode(bookingId);
        return paymentService.generatePDFTicket(bookingId);
    }

    public List<Booking> viewBookingHistory(int userId) {
        List<Booking> history = historyService.getUserBookings(userId);
        return historyService.sortBookingsByDateDesc(history);
    }

    public void createNewEvent(User currentUser, Event eventData) {
        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            adminService.createEvent(currentUser.getId(), eventData);
        }
    }

    public Report viewSalesReport(User currentUser) {
        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            return reportingService.generateSalesReport(currentUser.getId());
        }
        return null;
    }
}
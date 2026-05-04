package com.ticketpass.controller;


import com.ticketpass.model.User;
import com.ticketpass.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Covers requirements: T-SRS-TP-001.4, T-SRS-TP-010.2
public class AuthenticationIntegrationTest {

    private TicketPass ticketPass;

    @BeforeEach
    public void setup() {
        ticketPass = new TicketPass(
                new EventBrowsingService(),
                new SeatReservationService(),
                new PaymentAndTicketingService(),
                null,                       // QRCodeService null
                null,                                // PdfTicketService
                new AdminManagementService(),
                new ReportingService(),
                new BookingHistoryService()
        );
    }

    @Test
    @DisplayName("T-SRS-TP-001.4: Login Performance Standard (<= 3s)")
    public void loginAndRetrieveEvents_ValidCredentials_CompletesWithinThreeSeconds() {
        String user = "customer1";
        String pass = "cust123";

        long startTime = System.currentTimeMillis();

        User loggedInUser = ticketPass.login(user, pass);
        List<Event> events = ticketPass.getUpcomingEvents();

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;

        assertNotNull(loggedInUser, "Login should return a valid user object.");
        assertTrue(durationSeconds <= 3.0, "Performance must be under 3 seconds.");

        System.out.println("T-SRS-TP-001.4 Performance Result: " + durationSeconds + "s");
    }
}
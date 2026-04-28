package com.ticketpass.gui;

import com.ticketpass.controller.*;
import javax.swing.*;

public class TicketPassApp {
    public static void main(String[] args) {
        EventBrowsingService browsingService = new EventBrowsingService();
        SeatReservationService reservationService = new SeatReservationService();
        PaymentAndTicketingService paymentService = new PaymentAndTicketingService();
        AdminManagementService adminService = new AdminManagementService();
        ReportingService reportingService = new ReportingService();
        BookingHistoryService historyService = new BookingHistoryService();

        TicketPass ticketPass = new TicketPass(
                browsingService,
                reservationService,
                paymentService,
                adminService,
                reportingService,
                historyService
        );

        SwingUtilities.invokeLater(() -> {
            //new LoginWindow(ticketPass).setVisible(true);
        });
    }
}
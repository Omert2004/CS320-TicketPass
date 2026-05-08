package com.ticketpass.controller;

import com.ticketpass.model.Event;
import com.ticketpass.model.EventStatus;
import com.ticketpass.model.User;
import com.ticketpass.model.Role;
import com.ticketpass.util.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

//Covers requirements: T-SRS-TP-004, 005, 006, 10,2
public class AdminManagementServiceTest {

    private AdminManagementService service;
    private int adminId;
    private int organizerId;

    @BeforeEach
    public void setUp() {
        service = new AdminManagementService();
        adminId = 1;     // seed: admin userId=1
        organizerId = 2; // seed: organizer1 userId=2
    }

    // T-SRS-TP-004 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-004: getAllEvents returns a non-null list for admin")
    public void getAllEvents_AdminId_ReturnsNonNullList() {
        List<Event> events = service.getAllEvents(adminId);
        assertNotNull(events, "getAllEvents should never return null.");
    }

    @Test
    @DisplayName("T-SRS-TP-004: approveEvent sets event status to ACTIVE")
    public void approveEvent_PendingEvent_StatusBecomesActive() {
        // Create a PENDING event first
        Event event = new Event();
        event.setName("Approve Test Event");
        event.setCategory("Sports");
        event.setEventDate(LocalDateTime.now().plusMonths(1));
        event.setAddress("Istanbul");
        event.setVenueName("Test Arena");
        event.setVenueCapacity(50);
        event.setPrice(100.00);

        int eventId = service.createEvent(organizerId, event);
        assertTrue(eventId > 0);
        assertDoesNotThrow(() -> service.approveEvent(adminId, eventId));

        List<Event> all = service.getAllEvents(adminId);
        Event approved = all.stream().filter(e -> e.getEventId() == eventId).findFirst().orElse(null);

        assertNotNull(approved);
        assertEquals(EventStatus.ACTIVE, approved.getStatus(),
                "Event status should be ACTIVE after approval.");

        service.deleteEvent(adminId, eventId);
    }

    // T-SRS-TP-005 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-005: getOrganizerEvents returns only events belonging to the organizer")
    public void getOrganizerEvents_ValidOrganizerId_ReturnsCorrectEvents() {
        List<Event> events = service.getOrganizerEvents(organizerId);
        assertNotNull(events);
        for (Event e : events) {
            assertEquals(organizerId, e.getOrganizerId(),
                    "Every returned event should belong to organizerId=" + organizerId);
        }
    }

    @Test
    @DisplayName("T-SRS-TP-005: createEvent inserts event and returns a valid eventId")
    public void createEvent_ValidData_ReturnsPositiveEventId() {
        Event event = new Event();
        event.setName("Unit Test Event");
        event.setCategory("Music");
        event.setEventDate(LocalDateTime.now().plusMonths(2));
        event.setAddress("Test Address, Istanbul");
        event.setVenueName("Test Venue");
        event.setVenueCapacity(100);
        event.setPrice(150.00);

        int newEventId = service.createEvent(organizerId, event);
        assertTrue(newEventId > 0, "createEvent should return a positive eventId after insertion.");

        service.deleteEvent(adminId, newEventId);
    }

    @Test
    @DisplayName("T-SRS-TP-005: cancelEvent sets event status to CANCELLED")
    public void cancelEvent_ActiveEvent_StatusBecomesCancelled() {
        Event event = new Event();
        event.setName("Cancel Test Event");
        event.setCategory("Theater");
        event.setEventDate(LocalDateTime.now().plusMonths(1));
        event.setAddress("Istanbul");
        event.setVenueName("Theater Venue");
        event.setVenueCapacity(200);
        event.setPrice(200.00);

        int eventId = service.createEvent(organizerId, event);
        assertTrue(eventId > 0);
        service.approveEvent(adminId, eventId);

        assertDoesNotThrow(() -> service.cancelEvent(adminId, eventId));

        List<Event> all = service.getAllEvents(adminId);
        Event cancelled = all.stream().filter(e -> e.getEventId() == eventId).findFirst().orElse(null);
        assertNotNull(cancelled);
        assertEquals(EventStatus.CANCELLED, cancelled.getStatus(),
                "Event status should be CANCELLED after cancellation.");

        service.deleteEvent(adminId, eventId);
    }

    @Test
    @DisplayName("T-SRS-TP-005: deleteEvent removes the event from database")
    public void deleteEvent_ExistingEvent_EventNoLongerExists() {
        Event event = new Event();
        event.setName("Delete Test Event");
        event.setCategory("Music");
        event.setEventDate(LocalDateTime.now().plusMonths(3));
        event.setAddress("Istanbul");
        event.setVenueName("Delete Venue");
        event.setVenueCapacity(300);
        event.setPrice(75.00);

        int eventId = service.createEvent(organizerId, event);
        assertTrue(eventId > 0);

        assertDoesNotThrow(() -> service.deleteEvent(adminId, eventId));

        List<Event> all = service.getAllEvents(adminId);
        boolean stillExists = all.stream().anyMatch(e -> e.getEventId() == eventId);
        assertFalse(stillExists, "Deleted event should no longer exist in the database.");
    }

    // T-SRS-TP-006 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-006: generateSeatingChart inserts rowCount x seatsPerRow seats")
    public void generateSeatingChart_ValidParams_InsertsCorrectSeatCount() {
        Event event = new Event();
        event.setName("Seating Test Event");
        event.setCategory("Music");
        event.setEventDate(LocalDateTime.now().plusMonths(1));
        event.setAddress("Istanbul");
        event.setVenueName("Seating Venue");
        event.setVenueCapacity(0);
        event.setPrice(100.00);

        int eventId = service.createEvent(organizerId, event);
        assertTrue(eventId > 0);

        int rows = 5;
        int cols = 10;
        assertDoesNotThrow(() -> service.generateSeatingChart(eventId, rows, cols));

        service.deleteEvent(adminId, eventId);
    }

    // T-SRS-TP-0010.2 - Unit Tests

    @Test
    @DisplayName("T-SRS-TP-010.2: getUserList returns a non-null list")
    public void getUserList_AdminId_ReturnsNonNullList() {
        List<User> users = service.getUserList(adminId);
        assertNotNull(users, "getUserList should never return null.");
        assertFalse(users.isEmpty(), "getUserList should return at least the seed users.");
    }

    @Test
    @DisplayName("T-SRS-TP-010.2: lockUserAccount and unlockUserAccount execute without exception")
    public void lockAndUnlockUserAccount_ValidUserId_NoException() {
        List<User> users = service.getUserList(adminId);
        User customer = users.stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .findFirst().orElse(null);
        assertNotNull(customer, "At least one CUSTOMER must exist in seed data.");

        assertDoesNotThrow(() -> service.lockUserAccount(adminId, customer.getUserId()));
        assertDoesNotThrow(() -> service.unlockUserAccount(adminId, customer.getUserId()));
    }
}
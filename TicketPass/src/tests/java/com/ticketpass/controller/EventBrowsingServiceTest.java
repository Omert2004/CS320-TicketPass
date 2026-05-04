package com.ticketpass.controller;

import com.ticketpass.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EventBrowsingServiceTest {

    private EventBrowsingService browsingService;

    @BeforeEach
    public void setup() {
        browsingService = new EventBrowsingService();
    }

    @Test
    @DisplayName("T-SRS-TP-001.1: Verify multi-criteria search logic")
    public void searchEvents_ValidCategoryAndPrice_ReturnsFilteredList() {
        //Summer Rock Fest
        String category = "Music";
        double maxPrice = 400.00;

        List<Event> results = browsingService.searchEvents(category, null, null, maxPrice, null);

        assertNotNull(results, "Search results should not be null.");
        assertFalse(results.isEmpty(), "Search should return at least one event from seed data.");

        for (Event event : results) {
            assertEquals("Music", event.getCategory(), "Result category must match search criteria.");
            assertTrue(event.getPrice() <= maxPrice, "Result price must be within max limit.");
        }
    }

    @Test
    @DisplayName("T-SRS-TP-001.1: Verify search with no matches")
    public void searchEvents_PriceTooLow_ReturnsEmptyList() {
        // There is no event that its price is 10.
        String category = "Music";
        double maxPrice = 10.00;

        List<Event> results = browsingService.searchEvents(category, null, null, maxPrice, null);
        assertTrue(results.isEmpty(), "Search should return an empty list when no events match the price.");
    }

    @Test
    @DisplayName("T-SRS-TP-001.3: Search Performance Standard (<= 10s)")
    public void searchEvents_ValidSearchRequest_CompletesWithinTenSeconds() {
        String category = "Music";
        double maxPrice = 500.00;

        long startTime = System.currentTimeMillis();

        List<Event> results = browsingService.searchEvents(category, null, null, maxPrice, null);

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double durationSeconds = durationMillis / 1000.0;

        assertTrue(durationSeconds <= 10.0,
                "Search performance failed! Took " + durationSeconds + " seconds, but limit is 10s.");
        assertFalse(results.isEmpty(), "Search should return results to validate performance.");

        System.out.println("T-SRS-TP-001.3 Performance Result: " + durationSeconds + "s");
    }
}
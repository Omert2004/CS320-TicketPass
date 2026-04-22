package com.ticketpass.controller;

import com.ticketpass.model.Report;
import com.ticketpass.model.EventStats;

public class ReportingService {

    public Report generateSalesReport(int adminId) {
        return new Report();
    }

    public EventStats getEventStatistics(int adminId, int eventId) {
        return new EventStats();
    }
}
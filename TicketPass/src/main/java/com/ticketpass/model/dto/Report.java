package com.ticketpass.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class Report {
    private LocalDateTime generatedAt;
    private List<ReportItem> items;

    public Report() {
        this.generatedAt = LocalDateTime.now();
    }

    public Report(List<ReportItem> items) {
        this.generatedAt = LocalDateTime.now();
        this.items = items;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<ReportItem> getItems() {
        return items;
    }

    public void setItems(List<ReportItem> items) {
        this.items = items;
    }
}
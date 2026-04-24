-- ============================================================
--  TicketPass – Indexes
-- ============================================================
USE ticketpass;

CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_date     ON events(eventDate);
CREATE INDEX idx_events_status   ON events(status);
CREATE INDEX idx_seats_event     ON seats(eventId, status);
CREATE INDEX idx_seats_lock      ON seats(lockExpires);
CREATE INDEX idx_tickets_user    ON tickets(userId);
CREATE INDEX idx_tickets_event   ON tickets(eventId);
CREATE INDEX idx_txn_user        ON transactions(userId);
CREATE INDEX idx_txn_status      ON transactions(status);

-- ============================================================
--  TicketPass – Seed Data  (DEV / TEST only)
-- ============================================================
USE ticketpass;

INSERT INTO users(username, email, passwordHash, role) VALUES
    ('admin',     'admin@ticketpass.com',  SHA2('admin123', 256), 'ADMIN'),
    ('organizer1','org1@ticketpass.com',   SHA2('org123',   256), 'ORGANIZER'),
    ('customer1', 'cust1@ticketpass.com',  SHA2('cust123',  256), 'CUSTOMER'),
    ('customer2', 'cust2@ticketpass.com',  SHA2('cust123',  256), 'CUSTOMER');

INSERT INTO events(organizerId,name,category,eventDate,address,venueName,venueCapacity,price,status) VALUES
    (2,'Summer Rock Fest',          'Music',   '2026-07-15 20:00:00','Harbiye, Istanbul',   'Cemil Topuzlu Amphitheater',5000, 350.00,'ACTIVE'),
    (2,'Istanbul Jazz Night',       'Music',   '2026-08-02 21:00:00','Kadıköy, Istanbul',   'Kadıköy Arena',             2000, 200.00,'ACTIVE'),
    (2,'Galatasaray vs Fenerbahçe', 'Football','2026-09-20 19:00:00','Florya, Istanbul',    'RAMS Park',                50000, 500.00,'ACTIVE');

-- Seats A1-A5, B1-B5, C1-C5 for event 1
INSERT INTO seats(eventId,rowLabel,seatNumber,status)
SELECT 1, r.rowLabel, n.n, 'AVAILABLE'
FROM
    (SELECT 'A' rowLabel UNION SELECT 'B' UNION SELECT 'C') r
    CROSS JOIN
    (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n;

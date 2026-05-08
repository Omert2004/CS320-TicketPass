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
    (2,'Istanbul Jazz Night',       'Music',   '2026-08-02 21:00:00','Kadıköy, Istanbul',   'Kadıköy Arena',             2000, 200.00,'PENDING'),
    (2,'Galatasaray vs Fenerbahçe', 'Football','2026-09-20 19:00:00','Florya, Istanbul',    'RAMS Park',                50000, 500.00,'ACTIVE');

-- Seats A1-A5, B1-B5, C1-C5 for both Event 1 and Event 2
INSERT INTO seats(eventId, rowLabel, seatNumber, status)
SELECT e.eventId, r.rowLabel, n.n, 'AVAILABLE'
FROM
    (SELECT 1 AS eventId UNION SELECT 2) e
    CROSS JOIN (SELECT 'A' AS rowLabel UNION SELECT 'B' UNION SELECT 'C') r
    CROSS JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n
ORDER BY e.eventId, r.rowLabel, n.n;

-- ==============================================================================
-- TEST FIXTURE FOR T-SRS-TP-008 (Event Statistics)
-- Initial Inputs: Generates Event ID 888 (Capacity 100), 25 'SOLD' seats (Row A), 
-- 75 'AVAILABLE' seats (Rows B, C, D), and safely links 25 Tickets/Transactions.
-- ==============================================================================

-- STEP 1: Create the Mock Event
INSERT INTO events (eventId, organizerId, name, category, eventDate, address, venueName, venueCapacity, price, status) 
VALUES (888, 2, 'T-SRS-TP-008 Analytics Mock', 'Other', '2026-12-31 20:00:00', '123 QA Street', 'Testing Arena', 100, 50.00, 'ACTIVE');

-- STEP 2: Create a Mock Transaction (ID: 888) for User 3 (Customer)
INSERT INTO transactions (transactionId, userId, status) 
VALUES (888, 3, 'SUCCESS');

-- STEP 3a: Create 25 Mock Seats for Row A (SOLD)
INSERT INTO seats (seatId, eventId, rowLabel, seatNumber, status)
WITH RECURSIVE NumberLoop AS (
    SELECT 1 AS n UNION ALL SELECT n + 1 FROM NumberLoop WHERE n < 25
)
SELECT 8800 + n, 888, 'A', n, 'SOLD' FROM NumberLoop;

-- STEP 3b: Create 75 Empty Seats for Rows B, C, D (AVAILABLE)
INSERT INTO seats (eventId, rowLabel, seatNumber, status)
WITH RECURSIVE NumberLoop AS (
    SELECT 1 AS n UNION ALL SELECT n + 1 FROM NumberLoop WHERE n < 25
)
SELECT 888, r.rowLabel, s.n, 'AVAILABLE'
FROM (SELECT 'B' AS rowLabel UNION SELECT 'C' UNION SELECT 'D') r
CROSS JOIN NumberLoop s;

-- STEP 4: Insert 25 purchased tickets safely linking all Foreign Keys
INSERT INTO tickets (userId, eventId, seatId, transactionId, qrCode)
WITH RECURSIVE NumberLoop AS (
    SELECT 1 AS n UNION ALL SELECT n + 1 FROM NumberLoop WHERE n < 25
)
SELECT 3, 888, 8800 + n, 888, CONCAT('mock-qr-', n) FROM NumberLoop;


-- ==============================================================================
-- BOOKING FOR EVENT 1 (For T-SRS-TP-009 Booking History Test)
-- This script buys Seat A1 for Event 1 for customer1 (User ID 3).
-- ==============================================================================

-- 1. Create a successful transaction (ID will be generated automatically)
INSERT INTO transactions (userId, status) 
VALUES (3, 'SUCCESS');

-- 2. Mark Seat A1 in Event 1 as SOLD
UPDATE seats 
SET status = 'SOLD' 
WHERE eventId = 1 AND rowLabel = 'A' AND seatNumber = 1;

-- 3. Insert the Ticket record linking User 3 to Event 1 and Seat A1
INSERT INTO tickets (userId, eventId, seatId, transactionId, qrCode)
SELECT 
    3,               -- User ID (customer1)
    1,               -- Event ID
    s.seatId,        -- Finds the specific seat ID for A1
    LAST_INSERT_ID(),-- Automatically grabs the ID of the transaction we just made
    'QR-EVENT-1-A1'  -- Unique QR code for this ticket
FROM seats s
WHERE s.eventId = 1 AND s.rowLabel = 'A' AND s.seatNumber = 1
LIMIT 1;


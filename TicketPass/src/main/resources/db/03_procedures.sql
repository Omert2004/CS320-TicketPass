-- ============================================================
--  TicketPass – Stored Procedures
--  Grouped by Data Access interface (TP-SDD v1.12, Section 3.3)
-- ============================================================
USE ticketpass;

DELIMITER $$

-- -------------------------------------------------------
--  IUserManagement
-- -------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_login$$
CREATE PROCEDURE sp_login(IN p_username VARCHAR(50), IN p_passwordHash VARCHAR(255))
BEGIN
    SELECT userId, username, email, role, isLocked, failedAttempts
    FROM   users
    WHERE  username = p_username AND passwordHash = p_passwordHash;
END$$

DROP PROCEDURE IF EXISTS sp_incrementFailedAttempts$$
CREATE PROCEDURE sp_incrementFailedAttempts(IN p_userId INT)
BEGIN
    UPDATE users
    SET    failedAttempts = failedAttempts + 1,
           isLocked = IF(failedAttempts + 1 >= 4, TRUE, isLocked)
    WHERE  userId = p_userId;
END$$

DROP PROCEDURE IF EXISTS sp_resetFailedAttempts$$
CREATE PROCEDURE sp_resetFailedAttempts(IN p_userId INT)
BEGIN
    UPDATE users SET failedAttempts = 0, isLocked = FALSE WHERE userId = p_userId;
END$$

DROP PROCEDURE IF EXISTS sp_lockAccount$$
CREATE PROCEDURE sp_lockAccount(IN p_userId INT)
BEGIN
    UPDATE users SET isLocked = TRUE WHERE userId = p_userId;
END$$

DROP PROCEDURE IF EXISTS sp_verifyAdminRole$$
CREATE PROCEDURE sp_verifyAdminRole(IN p_userId INT, OUT p_isAdmin BOOLEAN)
BEGIN
    SELECT COUNT(*) > 0 INTO p_isAdmin
    FROM   users WHERE userId = p_userId AND role IN ('ADMIN','ORGANIZER');
END$$

DROP PROCEDURE IF EXISTS sp_getUserList$$
CREATE PROCEDURE sp_getUserList()
BEGIN
    SELECT userId, username, email, role, isLocked, failedAttempts, createdAt
    FROM   users ORDER BY username;
END$$


-- -------------------------------------------------------
--  IEventManagement
-- -------------------------------------------------------
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_getOrganizerEvents$$
CREATE PROCEDURE sp_getOrganizerEvents(IN p_organizerId INT)
BEGIN
    SELECT * FROM events 
    WHERE organizerId = p_organizerId
    ORDER BY eventDate DESC;
END$$

DROP PROCEDURE IF EXISTS sp_createEvent$$
CREATE PROCEDURE sp_createEvent(
    IN p_organizerId INT, IN p_name VARCHAR(150), IN p_category VARCHAR(80),
    IN p_eventDate DATETIME, IN p_address VARCHAR(255), IN p_venueName VARCHAR(150),
    IN p_venueCapacity INT, IN p_price DOUBLE)
BEGIN
    INSERT INTO events(organizerId,name,category,eventDate,address,venueName,venueCapacity,price,status)
    VALUES (p_organizerId,p_name,p_category,p_eventDate,p_address,p_venueName,p_venueCapacity,p_price,'PENDING');
    SELECT LAST_INSERT_ID() AS newEventId;
END$$

DROP PROCEDURE IF EXISTS sp_editEvent$$
CREATE PROCEDURE sp_editEvent(
    IN p_adminId INT, IN p_eventId INT, IN p_name VARCHAR(150), IN p_category VARCHAR(80),
    IN p_eventDate DATETIME, IN p_address VARCHAR(255), IN p_price DOUBLE,
    IN p_venueCapacity INT,IN p_status VARCHAR(50))
BEGIN
    UPDATE events SET name=p_name, category=p_category, eventDate=p_eventDate,
           address=p_address, price=p_price, venueCapacity = p_venueCapacity, status = p_status 
           WHERE eventId=p_eventId;
END$$

DROP PROCEDURE IF EXISTS sp_cancelEvent$$
CREATE PROCEDURE sp_cancelEvent(IN p_adminId INT, IN p_eventId INT)
BEGIN
    UPDATE events SET status='CANCELLED' WHERE eventId=p_eventId;
END$$

DROP PROCEDURE IF EXISTS sp_deleteEvent$$
CREATE PROCEDURE sp_deleteEvent(IN p_adminId INT, IN p_eventId INT)
BEGIN
    DELETE FROM events WHERE eventId=p_eventId;
END$$

DROP PROCEDURE IF EXISTS sp_approveEvent$$
CREATE PROCEDURE sp_approveEvent(IN p_adminId INT, IN p_eventId INT)
BEGIN
    UPDATE events SET status='ACTIVE' WHERE eventId=p_eventId;
END$$

DROP PROCEDURE IF EXISTS sp_getUpcomingEvents$$
CREATE PROCEDURE sp_getUpcomingEvents()
BEGIN
    SELECT * FROM events WHERE status='ACTIVE' AND eventDate > NOW() ORDER BY eventDate ASC;
END$$

DROP PROCEDURE IF EXISTS sp_getEventsByFilter$$
CREATE PROCEDURE sp_getEventsByFilter(
    IN p_category VARCHAR(80), IN p_date DATE, IN p_location VARCHAR(255),
    IN p_price DOUBLE, IN p_artist VARCHAR(150))
BEGIN
    SELECT * FROM events WHERE status='ACTIVE'
      AND (p_category IS NULL OR category = p_category)
      AND (p_date     IS NULL OR DATE(eventDate) = p_date)
      AND (p_location IS NULL OR address LIKE CONCAT('%',p_location,'%'))
      AND (p_price    IS NULL OR price <= p_price)
      AND (p_artist   IS NULL OR name  LIKE CONCAT('%',p_artist,'%'))
    ORDER BY eventDate ASC;
END$$

DROP PROCEDURE IF EXISTS sp_getEventDetails$$
CREATE PROCEDURE sp_getEventDetails(IN p_eventId INT)
BEGIN
    SELECT e.*,
           (SELECT COUNT(*) FROM seats WHERE eventId=p_eventId AND status='AVAILABLE') AS availableSeats
    FROM   events e WHERE e.eventId=p_eventId;
END$$

DROP PROCEDURE IF EXISTS sp_getEventStats$$
CREATE PROCEDURE sp_getEventStats(IN p_adminId INT, IN p_eventId INT)
BEGIN
    SELECT e.eventId, e.venueCapacity,
           COUNT(t.ticketId) AS seatsPurchased,
           ROUND(COUNT(t.ticketId)/e.venueCapacity*100,2) AS occupancyRate,
           ROUND(COUNT(t.ticketId)*e.price,2) AS expectedRevenue
    FROM   events e LEFT JOIN tickets t ON t.eventId=e.eventId
    WHERE  e.eventId=p_eventId GROUP BY e.eventId, e.venueCapacity, e.price;
END$$


-- -------------------------------------------------------
--  ISeatManagement
-- -------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_getSeatsByEvent$$
CREATE PROCEDURE sp_getSeatsByEvent(IN p_eventId INT)
BEGIN
    SELECT seatId,eventId,rowLabel,seatNumber,status,lockedBy,lockExpires
    FROM   seats WHERE eventId=p_eventId ORDER BY rowLabel, seatNumber;
END$$

DROP PROCEDURE IF EXISTS sp_lockSeat$$
CREATE PROCEDURE sp_lockSeat(IN p_seatId INT, IN p_userId INT, OUT p_success BOOLEAN)
BEGIN
    DECLARE v_status VARCHAR(20);
    START TRANSACTION;
    SELECT status INTO v_status FROM seats WHERE seatId=p_seatId FOR UPDATE;
    IF v_status = 'AVAILABLE' THEN
        UPDATE seats SET status='LOCKED', lockedBy=p_userId,
               lockExpires=DATE_ADD(NOW(), INTERVAL 10 MINUTE) WHERE seatId=p_seatId;
        SET p_success = TRUE;
    ELSE
        SET p_success = FALSE;
    END IF;
    COMMIT;
END$$

DROP PROCEDURE IF EXISTS sp_releaseSeat$$
CREATE PROCEDURE sp_releaseSeat(IN p_seatId INT)
BEGIN
    UPDATE seats SET status='AVAILABLE', lockedBy=NULL, lockExpires=NULL WHERE seatId=p_seatId;
END$$

DROP PROCEDURE IF EXISTS sp_blockSeat$$
CREATE PROCEDURE sp_blockSeat(IN p_seatId INT)
BEGIN
    UPDATE seats SET status='BLOCKED', lockedBy=NULL, lockExpires=NULL WHERE seatId=p_seatId;
END$$

DROP PROCEDURE IF EXISTS sp_releaseExpiredLocks$$
CREATE PROCEDURE sp_releaseExpiredLocks()
BEGIN
    UPDATE seats SET status='AVAILABLE', lockedBy=NULL, lockExpires=NULL
    WHERE  status='LOCKED' AND lockExpires < NOW();
END$$

DROP PROCEDURE IF EXISTS sp_updateSeatAvailability$$
CREATE PROCEDURE sp_updateSeatAvailability(IN p_adminId INT, IN p_seatId INT, IN p_status VARCHAR(20))
BEGIN
    UPDATE seats SET status=p_status WHERE seatId=p_seatId;
END$$


-- -------------------------------------------------------
--  ITicketManagement
-- -------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_generateTicket$$
CREATE PROCEDURE sp_generateTicket(
    IN p_userId INT, IN p_eventId INT, IN p_seatId INT,
    IN p_transactionId INT, IN p_qrCode VARCHAR(512), OUT p_ticketId INT)
BEGIN
    INSERT INTO tickets(userId,eventId,seatId,transactionId,qrCode)
    VALUES (p_userId,p_eventId,p_seatId,p_transactionId,p_qrCode);
    SET p_ticketId = LAST_INSERT_ID();
    UPDATE seats SET status='SOLD', lockedBy=NULL, lockExpires=NULL WHERE seatId=p_seatId;
END$$

DROP PROCEDURE IF EXISTS sp_getTicketsByUser$$
CREATE PROCEDURE sp_getTicketsByUser(IN p_userId INT, IN p_sortBy VARCHAR(30))
BEGIN
    SELECT tk.ticketId, tk.purchaseTime, tk.qrCode, tk.pdfPath,
           e.name AS eventName, e.eventDate, e.address, e.venueName,
           s.rowLabel, s.seatNumber
    FROM   tickets tk
    JOIN   events e ON e.eventId=tk.eventId
    JOIN   seats  s ON s.seatId=tk.seatId
    WHERE  tk.userId=p_userId
    ORDER BY
        CASE WHEN p_sortBy='DATE_ASC' THEN e.eventDate END ASC,
        CASE WHEN p_sortBy IS NULL OR p_sortBy='DATE_DESC' THEN e.eventDate END DESC;
END$$


-- -------------------------------------------------------
--  ITransactionManagement
-- -------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_processPayment$$
CREATE PROCEDURE sp_processPayment(
    IN p_userId INT, IN p_lastFourDigits VARCHAR(4),
    IN p_token VARCHAR(255), IN p_status VARCHAR(10), OUT p_transactionId INT)
BEGIN
    INSERT INTO transactions(userId,lastFourDigits,token,status)
    VALUES (p_userId,p_lastFourDigits,p_token,p_status);
    SET p_transactionId = LAST_INSERT_ID();
END$$

DROP PROCEDURE IF EXISTS sp_getTransactionStatus$$
CREATE PROCEDURE sp_getTransactionStatus(IN p_transactionId INT)
BEGIN
    SELECT status FROM transactions WHERE transactionId=p_transactionId;
END$$

DROP PROCEDURE IF EXISTS sp_getTicketsByTransaction$$
CREATE PROCEDURE sp_getTicketsByTransaction(IN p_transactionId INT)
BEGIN
    SELECT * FROM tickets WHERE transactionId=p_transactionId;
END$$

DROP PROCEDURE IF EXISTS sp_getSalesReport$$
CREATE PROCEDURE sp_getSalesReport(IN p_adminId INT)
BEGIN
    SELECT e.eventId, e.name AS eventName, e.eventDate, e.price,
           COUNT(t.ticketId) AS ticketsSold,
           ROUND(COUNT(t.ticketId)/e.venueCapacity*100,2) AS occupancyRate,
           ROUND(COUNT(t.ticketId)*e.price,2) AS totalRevenue
    FROM   events e LEFT JOIN tickets t ON t.eventId=e.eventId
    WHERE  e.status != 'CANCELLED'
    GROUP  BY e.eventId, e.name, e.eventDate, e.price, e.venueCapacity
    ORDER  BY e.eventDate DESC;
END$$

DELIMITER ;

-- Scheduled event: auto-release expired locks every minute (SRS-TP-003.1)
SET GLOBAL event_scheduler = ON;
DROP EVENT IF EXISTS evt_releaseExpiredLocks;
CREATE EVENT evt_releaseExpiredLocks
    ON SCHEDULE EVERY 1 MINUTE DO CALL sp_releaseExpiredLocks();

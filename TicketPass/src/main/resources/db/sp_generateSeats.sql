-- ============================================================
--  sp_generateSeats  (SRS-TP-005)
--  Generates seats for an event: rows A-Z, seats 1-N
--  CALL sp_generateSeats(1, 5, 10)  → rows A-E, seats 1-10 = 50 seats
-- ============================================================
USE ticketpass;

DROP PROCEDURE IF EXISTS sp_generateSeats;

DELIMITER $$
CREATE PROCEDURE sp_generateSeats(
    IN p_eventId    INT,
    IN p_rowCount   INT,
    IN p_seatsPerRow INT
)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 0;
    DECLARE rowLabel VARCHAR(10);

    DELETE FROM seats WHERE eventId = p_eventId;

    SET i = 0;
    WHILE i < p_rowCount DO
        SET rowLabel = CHAR(65 + i);
        SET j = 1;
        WHILE j <= p_seatsPerRow DO
            INSERT INTO seats(eventId, rowLabel, seatNumber, status)
            VALUES (p_eventId, rowLabel, j, 'AVAILABLE');
            SET j = j + 1;
        END WHILE;
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
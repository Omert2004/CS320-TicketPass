-- ============================================================
--  TicketPass – Database Schema
--  MySQL 8.4.8  |  TP-SRS v2.8 / TP-SDD v1.12
-- ============================================================

CREATE DATABASE IF NOT EXISTS ticketpass
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ticketpass;

-- users
CREATE TABLE IF NOT EXISTS users (
    userId          INT            NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)    NOT NULL UNIQUE,
    email           VARCHAR(100)   NOT NULL UNIQUE,
    passwordHash    VARCHAR(255)   NOT NULL,
    role            ENUM('CUSTOMER','ORGANIZER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    isLocked        BOOLEAN        NOT NULL DEFAULT FALSE,
    failedAttempts  INT            NOT NULL DEFAULT 0,
    createdAt       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userId)
) ENGINE=InnoDB;

-- events
CREATE TABLE IF NOT EXISTS events (
    eventId         INT            NOT NULL AUTO_INCREMENT,
    organizerId     INT            NOT NULL,
    name            VARCHAR(150)   NOT NULL,
    category        VARCHAR(80)    NOT NULL,
    eventDate       DATETIME       NOT NULL,
    address         VARCHAR(255)   NOT NULL,
    venueName       VARCHAR(150)   NOT NULL,
    venueCapacity   INT            NOT NULL,
    price           DOUBLE         NOT NULL,
    status          ENUM('PENDING','ACTIVE','CANCELLED') NOT NULL DEFAULT 'PENDING',
    createdAt       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (eventId),
    CONSTRAINT fk_event_organizer FOREIGN KEY (organizerId) REFERENCES users(userId)
) ENGINE=InnoDB;

-- seats
CREATE TABLE IF NOT EXISTS seats (
    seatId          INT            NOT NULL AUTO_INCREMENT,
    eventId         INT            NOT NULL,
    rowLabel        VARCHAR(10)    NOT NULL,
    seatNumber      INT            NOT NULL,
    status          ENUM('AVAILABLE','LOCKED','SOLD','BLOCKED') NOT NULL DEFAULT 'AVAILABLE',
    lockedBy        INT            NULL,
    lockExpires     DATETIME       NULL,
    PRIMARY KEY (seatId),
    CONSTRAINT fk_seat_event FOREIGN KEY (eventId)  REFERENCES events(eventId),
    CONSTRAINT fk_seat_user  FOREIGN KEY (lockedBy) REFERENCES users(userId),
    UNIQUE KEY uq_seat_position (eventId, rowLabel, seatNumber)
) ENGINE=InnoDB;

-- transactions  (no raw card data – SRS-TP-003.2)
CREATE TABLE IF NOT EXISTS transactions (
    transactionId   INT            NOT NULL AUTO_INCREMENT,
    userId          INT            NOT NULL,
    lastFourDigits  VARCHAR(4)     NULL,
    token           VARCHAR(255)   NULL,
    status          ENUM('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
    timestamp       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transactionId),
    CONSTRAINT fk_txn_user FOREIGN KEY (userId) REFERENCES users(userId)
) ENGINE=InnoDB;

-- tickets
CREATE TABLE IF NOT EXISTS tickets (
    ticketId        INT            NOT NULL AUTO_INCREMENT,
    userId          INT            NOT NULL,
    eventId         INT            NOT NULL,
    seatId          INT            NOT NULL,
    transactionId   INT            NOT NULL,
    purchaseTime    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    qrCode          VARCHAR(512)   NOT NULL,
    pdfPath         VARCHAR(512)   NULL,
    PRIMARY KEY (ticketId),
    CONSTRAINT fk_ticket_user  FOREIGN KEY (userId)        REFERENCES users(userId),
    CONSTRAINT fk_ticket_event FOREIGN KEY (eventId)       REFERENCES events(eventId),
    CONSTRAINT fk_ticket_seat  FOREIGN KEY (seatId)        REFERENCES seats(seatId),
    CONSTRAINT fk_ticket_txn   FOREIGN KEY (transactionId) REFERENCES transactions(transactionId)
) ENGINE=InnoDB;

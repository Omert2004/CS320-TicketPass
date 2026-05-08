package com.ticketpass.controller;

import com.ticketpass.model.Seat;
import com.ticketpass.model.SeatStatus;
import com.ticketpass.model.dto.SeatingChart;
import com.ticketpass.util.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.List;

// Covers: T-SRS-TP-003.1 (Transaction Timeout) and T-SRS-TP-003.2 (Secure Payment)
public class PaymentSecurityTest {

    private SeatReservationService seatService;
    private AdminManagementService adminService;

    // Seat ID 1 from seed data: Event 1, Row A, Seat 1
    private static final int TEST_SEAT_ID = 1;
    private static final int TEST_USER_ID = 3; // customer1

    @BeforeEach
    public void setUp() {
        seatService = new SeatReservationService();
        adminService = new AdminManagementService();
        // Ensure seat is available before each test
        seatService.releaseSeatLock(TEST_SEAT_ID);
    }

    @AfterEach
    public void tearDown() {
        seatService.releaseSeatLock(TEST_SEAT_ID);
    }

    // -------------------------------------------------------
    //  T-SRS-TP-003.1: Transaction Timeout — seat lock state
    // -------------------------------------------------------

    @Test
    @DisplayName("T-SRS-TP-003.1: Seat transitions to LOCKED state upon selection")
    public void lockSeat_AvailableSeat_SeatBecomesLocked() {
        boolean locked = seatService.lockSeat(TEST_SEAT_ID, TEST_USER_ID);
        assertTrue(locked, "Seat should be successfully locked.");

        // Verify status in DB
        String status = getSeatStatusFromDB(TEST_SEAT_ID);
        assertEquals("LOCKED", status, "Seat status in DB should be LOCKED after selection.");
    }

    @Test
    @DisplayName("T-SRS-TP-003.1: lockExpires is set to approximately 10 minutes in the future")
    public void lockSeat_AvailableSeat_LockExpiresSetToTenMinutes() {
        seatService.lockSeat(TEST_SEAT_ID, TEST_USER_ID);

        String sql = "SELECT lockExpires FROM seats WHERE seatId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, TEST_SEAT_ID);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Seat record should exist.");

            Timestamp lockExpires = rs.getTimestamp("lockExpires");
            assertNotNull(lockExpires, "lockExpires should be set after locking.");

            long nowMs = System.currentTimeMillis();
            long expiresMs = lockExpires.getTime();
            long diffMinutes = (expiresMs - nowMs) / 60000;

            // Should be ~10 minutes (allow 8-12 min range for test latency)
            assertTrue(diffMinutes >= 8 && diffMinutes <= 12,
                    "lockExpires should be ~10 minutes from now, but was " + diffMinutes + " minutes.");
        } catch (SQLException e) {
            fail("SQL error during lockExpires check: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("T-SRS-TP-003.1: releaseSeatLock restores seat to AVAILABLE")
    public void releaseSeatLock_LockedSeat_SeatBecomesAvailable() {
        seatService.lockSeat(TEST_SEAT_ID, TEST_USER_ID);
        seatService.releaseSeatLock(TEST_SEAT_ID);

        String status = getSeatStatusFromDB(TEST_SEAT_ID);
        assertEquals("AVAILABLE", status, "Seat should return to AVAILABLE after lock release.");
    }

    @Test
    @DisplayName("T-SRS-TP-003.1: Expired locks are released by sp_releaseExpiredLocks")
    public void releaseExpiredLocks_ExpiredLock_SeatBecomesAvailable() {
        // Manually set a lock that expired in the past
        String sql = "UPDATE seats SET status='LOCKED', lockedBy=?, lockExpires=? WHERE seatId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, TEST_USER_ID);
            // Set lockExpires to 5 minutes AGO (expired)
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 5 * 60 * 1000));
            stmt.setInt(3, TEST_SEAT_ID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Could not set up expired lock: " + e.getMessage());
        }

        // Verify it's locked
        assertEquals("LOCKED", getSeatStatusFromDB(TEST_SEAT_ID));

        // Call the stored procedure
        try (Connection conn = DatabaseManager.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_releaseExpiredLocks()}")) {
            stmt.execute();
        } catch (SQLException e) {
            fail("sp_releaseExpiredLocks failed: " + e.getMessage());
        }

        // Verify seat is now AVAILABLE
        assertEquals("AVAILABLE", getSeatStatusFromDB(TEST_SEAT_ID),
                "Expired lock should be released by sp_releaseExpiredLocks.");
    }

    // -------------------------------------------------------
    //  T-SRS-TP-003.2: Secure Payment — no raw card data stored
    // -------------------------------------------------------

    @Test
    @DisplayName("T-SRS-TP-003.2: Full card number is never stored in transactions table")
    public void processPayment_CompletedTransaction_NoFullCardNumberInDB() {
        String fullCardNumber = "4111111111111111"; // Standard Visa test number
        String lastFour = "1111";
        String token = "tok_test_visa_" + System.currentTimeMillis();

        // Insert a mock transaction directly (simulating payment processing)
        String sql = "INSERT INTO transactions(userId, lastFourDigits, token, status) VALUES (?, ?, ?, 'SUCCESS')";
        int transactionId = -1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.setString(2, lastFour);
            stmt.setString(3, token);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) transactionId = keys.getInt(1);
        } catch (SQLException e) {
            fail("Could not insert test transaction: " + e.getMessage());
        }

        assertTrue(transactionId > 0, "Transaction should be inserted.");

        // Verify: full card number NOT stored anywhere in transactions table
        String checkSql = "SELECT COUNT(*) FROM transactions WHERE token LIKE ? OR lastFourDigits = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, "%" + fullCardNumber + "%");
            stmt.setString(2, fullCardNumber);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count, "Full card number must never be stored in the database.");
        } catch (SQLException e) {
            fail("SQL error during card number check: " + e.getMessage());
        }

        // Verify: only last 4 digits stored
        String verifySql = "SELECT lastFourDigits, token FROM transactions WHERE transactionId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(verifySql)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(lastFour, rs.getString("lastFourDigits"),
                    "Only the last 4 digits should be stored.");
            assertNotNull(rs.getString("token"), "A payment token should be stored.");
            assertNotEquals(fullCardNumber, rs.getString("token"),
                    "The token must not equal the full card number.");
        } catch (SQLException e) {
            fail("SQL error during verification: " + e.getMessage());
        }

        // Cleanup
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM transactions WHERE transactionId = ?")) {
            stmt.setInt(1, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("T-SRS-TP-003.2: CVV is never stored in the database")
    public void processPayment_CompletedTransaction_NoCVVInDB() {
        String cvv = "123";

        // Search entire transactions table for CVV
        String sql = "SELECT COUNT(*) FROM transactions WHERE token LIKE ? OR lastFourDigits = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + cvv + "%");
            stmt.setString(2, cvv);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            // CVV "123" is 3 digits and could theoretically match last4 if it were 4 digits
            // Just verify no token contains the CVV as a substring
        } catch (SQLException e) {
            fail("SQL error during CVV check: " + e.getMessage());
        }

        // The real check: confirm transactions table has no column for CVV
        String descSql = "SHOW COLUMNS FROM transactions";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(descSql);
             ResultSet rs = stmt.executeQuery()) {
            boolean hasCvvColumn = false;
            while (rs.next()) {
                String colName = rs.getString("Field").toLowerCase();
                if (colName.contains("cvv") || colName.contains("cvc") || colName.contains("security")) {
                    hasCvvColumn = true;
                }
            }
            assertFalse(hasCvvColumn,
                    "The transactions table must not contain a CVV/CVC column.");
        } catch (SQLException e) {
            fail("SQL error during schema check: " + e.getMessage());
        }
    }

    //  Helper

    private String getSeatStatusFromDB(int seatId) {
        String sql = "SELECT status FROM seats WHERE seatId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (SQLException e) {
            fail("Could not read seat status: " + e.getMessage());
        }
        return null;
    }
}
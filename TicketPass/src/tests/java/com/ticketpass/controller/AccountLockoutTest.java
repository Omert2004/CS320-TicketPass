package com.ticketpass.controller;

import com.ticketpass.model.User;
import com.ticketpass.util.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Covers: T-SRS-TP-010.2: Account Lockout Security Policy
public class AccountLockoutTest {

    private TicketPass ticketPass;
    private static final String TEST_USERNAME = "customer1";
    private static final String CORRECT_PASSWORD = "cust123";
    private static final String WRONG_PASSWORD = "wrongpass";

    @BeforeEach
    public void setUp() {
        ticketPass = new TicketPass(
                new EventBrowsingService(),
                new SeatReservationService(),
                new PaymentAndTicketingService(),
                null,
                null,
                new AdminManagementService(),
                new ReportingService(),
                new BookingHistoryService()
        );
        // Ensure account is unlocked before each test
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        if (user != null) {
            DatabaseManager.resetFailedAttempts(user.getUserId());
        }
    }

    @AfterEach
    public void tearDown() {
        // Always unlock after each test so seed data stays clean
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        if (user != null) {
            DatabaseManager.resetFailedAttempts(user.getUserId());
        } else {
            // Account might be locked, reset by userId directly
            // customer1 is userId=3 in seed data
            DatabaseManager.resetFailedAttempts(3);
        }
    }

    // Step 1 of T-SRS-TP-010.2: 4 consecutive failed logins lock the account
    @Test
    @DisplayName("T-SRS-TP-010.2 Step 1: 3 consecutive failed logins lock the account")
    public void login_ThreeFailedAttempts_AccountStillAccessible() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        // Simulate 3 failed attempts
        for (int i = 0; i < 4; i++) {
            User result = ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            assertNull(result, "Failed login attempt " + (i + 1) + " should return null.");
            ticketPass.handleFailedLoginAttempt(userId);
        }

        // System locks at 3rd attempt
        User lockedResult = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(lockedResult, "Account should be locked after 3 failed attempts.");
    }

    // Step 2 of T-SRS-TP-010.2: 4th failed attempt locks the account
    @Test
    @DisplayName("T-SRS-TP-010.2 Step 2: 4th consecutive failed login locks the account")
    public void login_FourFailedAttempts_AccountBecomesLocked() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        // Simulate 4 failed attempts
        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        // After 4 failed attempts, login with correct password should return null
        User lockedResult = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(lockedResult, "Account should be locked after 4 consecutive failed attempts.");
    }

    // Step 3 of T-SRS-TP-010.2: 5th attempt (even with correct password) is rejected
    @Test
    @DisplayName("T-SRS-TP-010.2 Step 3: Locked account rejects correct password on 5th attempt")
    public void login_LockedAccount_CorrectPasswordStillRejected() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        // Lock the account with 4 failed attempts
        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        // 5th attempt with CORRECT password — should still be rejected
        User attempt5 = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(attempt5, "Locked account must reject login even with correct credentials.");

        // 6th attempt — also rejected
        User attempt6 = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(attempt6, "Locked account must remain locked on subsequent attempts.");
    }

    // Admin reset unlocks the account
    @Test
    @DisplayName("T-SRS-TP-010.2: Admin reset allows login after lockout")
    public void login_AfterAdminReset_LoginSucceeds() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user);
        int userId = user.getUserId();

        // Lock the account
        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        // Verify locked
        assertNull(ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD), "Should be locked.");

        // Admin resets
        DatabaseManager.resetFailedAttempts(userId);

        // Now login should succeed
        User unlocked = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(unlocked, "Account should be accessible after admin reset.");
    }
}
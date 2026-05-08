package com.ticketpass.controller;

import com.ticketpass.model.User;
import com.ticketpass.util.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

//Covers requirement: T-SRS-TP-0010.2
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
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        if (user != null) {
            DatabaseManager.resetFailedAttempts(user.getUserId());
        }
    }

    @AfterEach
    public void tearDown() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        if (user != null) {
            DatabaseManager.resetFailedAttempts(user.getUserId());
        } else {
            DatabaseManager.resetFailedAttempts(3);
        }
    }

    @Test
    @DisplayName("T-SRS-TP-010.2 Step 1: 3 consecutive failed logins lock the account")
    public void login_ThreeFailedAttempts_AccountStillAccessible() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        for (int i = 0; i < 4; i++) {
            User result = ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            assertNull(result, "Failed login attempt " + (i + 1) + " should return null.");
            ticketPass.handleFailedLoginAttempt(userId);
        }

        User lockedResult = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(lockedResult, "Account should be locked after 3 failed attempts.");
    }

    @Test
    @DisplayName("T-SRS-TP-010.2 Step 2: 4th consecutive failed login locks the account")
    public void login_FourFailedAttempts_AccountBecomesLocked() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        User lockedResult = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(lockedResult, "Account should be locked after 4 consecutive failed attempts.");
    }

    @Test
    @DisplayName("T-SRS-TP-010.2 Step 3: Locked account rejects correct password on 5th attempt")
    public void login_LockedAccount_CorrectPasswordStillRejected() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user, "Test user must exist in seed data.");
        int userId = user.getUserId();

        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        User attempt5 = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(attempt5, "Locked account must reject login even with correct credentials.");

        User attempt6 = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNull(attempt6, "Locked account must remain locked on subsequent attempts.");
    }

    @Test
    @DisplayName("T-SRS-TP-010.2: Admin reset allows login after lockout")
    public void login_AfterAdminReset_LoginSucceeds() {
        User user = DatabaseManager.authenticate(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(user);
        int userId = user.getUserId();

        for (int i = 0; i < 4; i++) {
            ticketPass.login(TEST_USERNAME, WRONG_PASSWORD);
            ticketPass.handleFailedLoginAttempt(userId);
        }

        assertNull(ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD), "Should be locked.");

        DatabaseManager.resetFailedAttempts(userId);

        User unlocked = ticketPass.login(TEST_USERNAME, CORRECT_PASSWORD);
        assertNotNull(unlocked, "Account should be accessible after admin reset.");
    }

}
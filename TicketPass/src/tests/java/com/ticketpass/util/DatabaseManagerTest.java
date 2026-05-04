package com.ticketpass.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    @Test
    @DisplayName("Infrastructure: Verify MySQL Database Connectivity")
    public void getConnection_DefaultConfiguration_ReturnsValidConnection() {

        try (Connection conn = DatabaseManager.getConnection()) {

            assertNotNull(conn, "The database connection should not be null.");
            assertFalse(conn.isClosed(), "The database connection should be open and active.");

        } catch (SQLException e) {
            fail("SQLException was thrown during connection attempt: " + e.getMessage());
        }
    }
}

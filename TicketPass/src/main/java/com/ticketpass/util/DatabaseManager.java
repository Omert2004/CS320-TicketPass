package com.ticketpass.util;

import com.ticketpass.model.User;
import com.ticketpass.model.Role;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/ticketpass";
    private static final String USER = "root";
    private static final String PASSWORD = "asdnjdbjhdbnsajkdb2193857**189AA";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static User authenticate(String username, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);

        String query = "{CALL sp_login(?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {

            cstmt.setString(1, username);
            cstmt.setString(2, hashedPassword);

            ResultSet rs = cstmt.executeQuery();

            if (rs.next()) {
                Role userRole = Role.valueOf(rs.getString("role").toUpperCase());

                return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("email"),
                        hashedPassword,
                        userRole,
                        rs.getBoolean("isLocked"),
                        rs.getInt("failedAttempts"),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(String username, String email, String password, String role) {
        String hashedPassword = PasswordHasher.hashPassword(password);

        String query = "INSERT INTO users (username, email, passwordHash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, role.toUpperCase());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Username or Email already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void incrementFailedAttempts(int userId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL sp_incrementFailedAttempts(?)}")) {
            cstmt.setInt(1, userId);
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetFailedAttempts(int userId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL sp_resetFailedAttempts(?)}")) {
            cstmt.setInt(1, userId);
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void lockAccount(int userId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL sp_lockAccount(?)}")) {
            cstmt.setInt(1, userId);
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
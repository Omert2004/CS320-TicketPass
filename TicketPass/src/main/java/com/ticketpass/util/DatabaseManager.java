import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

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
        String query = "SELECT UserID, Username, UserRole, FirstName, LastName FROM USERS WHERE Username = ? AND PasswordHash = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("UserRole"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(String username, String email, String password, String role, String firstName,
                                       String lastName) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        String query = "INSERT INTO USERS (Username, Email, PasswordHash, UserRole, FirstName, LastName) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, role);
            pstmt.setString(5, firstName);
            pstmt.setString(6, lastName);

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

}

// DBConnectionManager.DatabaseConnectionManager.java

package DBConnectionManager;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Driver;

public class DatabaseConnectionManager {
    private static Connection connection;
    private static final Object lock = new Object(); // Thread safety

    // Get or create a single connection
    public static Connection getConnection (String dbPath) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    // Close the connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Set to null for reinitialization
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}


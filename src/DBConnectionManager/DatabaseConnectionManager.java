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
        if (connection == null || connection.isClosed()) {
            synchronized (lock) { // Thread safety
                if (connection == null || connection.isClosed()) {
                    try {
                        Class.forName("org.sqlite.JDBC");
                        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

                        // Enable WAL mode for better performance
                        connection.createStatement().execute("PRAGMA journal_mode=WAL;");
                    } catch (ClassNotFoundException e) {
                        throw new SQLException("SQLite JDBC driver not found", e);
                    }
                }
            }
        }
        return connection;
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


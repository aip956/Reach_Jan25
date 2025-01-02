package DAO;

import java.sql.SQLException;

public class DAOFactory {
    public static GameDataDAO createGameDataDAO(String dbType, String dbPath) throws SQLException {
        switch (dbType.toLowerCase()) {
            case "sqlite":
                return new SQLiteGameDataDAO(dbPath); // Current
            case "mysql":
                // Placeholder for MySqLGameDataDAO implementation
                // return new MySQLGameDataDAO(dbPath);
                throw new UnsupportedOperationException("MySQL DAO not implemented yet");
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }
}
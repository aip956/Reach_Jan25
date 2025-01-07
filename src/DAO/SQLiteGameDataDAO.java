/* DAO.SQLiteGameDataDAO.java
Data Access Object; Handles all database interactions related to GameData,
    abstracting away the specifics of data persistence.
*/
// To add leaderboard, add method to retrieve top N players
// Indices set on fields for WHERE or ORDER BY
package DAO;
import Models.Game;
import Models.Guesser;
import Models.PlayerLevel;
import DBConnectionManager.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class SQLiteGameDataDAO implements GameDataDAO {
    private final String dbPath;
  

    public SQLiteGameDataDAO(String dbPath) throws SQLException {
        this.dbPath = dbPath; // Savd db as a class member
        ensureTableExists(); // Ensure a table is created when DAO instantiates
    }


    private void ensureTableExists() throws SQLException {
        System.out.println("Database path: " + dbPath);

        String createGameTableSQL = 
            "CREATE TABLE IF NOT EXISTS  game_data (" +
                "game_id INTEGER NOT NULL, " +
                "player_name TEXT NOT NULL, " +
                "player_attempts INTEGER NOT NUll, " + 
                "player_level TEXT NOT NULL, " +
                "solved BOOLEAN NOT NULL, " +
                "timestamp TEXT NOT NULL, " +
                "secret_code TEXT NOT NULL, " +
                "guesses TEXT NOT NULL, " +
                "PRIMARY KEY (game_id, player_name) " + // Composit key for no duplicate rows
            ");";

        try (Connection conn = DatabaseConnectionManager.getConnection(dbPath);
            // Create game_data table
            PreparedStatement gameTableStmt = conn.prepareStatement(createGameTableSQL)) {
                gameTableStmt.execute(); // Execute table creation
                // conn.commit(); // Commit after table creation
        } catch (SQLException e) {
            System.err.println("Error connecting to db: " + e.getMessage());
            throw e; // Rethrow the exception to signal failure
        }
    }

    @Override
    public void saveGameData(Game game) throws SQLException {
        // Query for insertion
        String gameSql = "INSERT INTO game_data (" +
            "game_id, " +
            "player_name, " +
            "player_attempts, " +
            "player_level, " +
            "solved, " +
            "timestamp, " +
            "secret_code, " + 
            "guesses) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // placeholders for parameterized queries

        // Establish DB connection; opens connection to db using DBConnMgr
        // Disables autocommit so if any operation fails, transaction rolls back
        try (Connection conn = DatabaseConnectionManager.getConnection(dbPath)) {
            conn.setAutoCommit(false); // Enable transaction for consistency
            
            // New game; finds max game_id in the game_data table; adds 1 to max val to generate an id
            // if no rows exist in table, IFNULL ensures start val = 1
            int gameID;

            try (PreparedStatement stmt = conn.prepareStatement("SELECT IFNULL (MAX(game_id), 0) + 1 AS new_game_id FROM game_data");
                ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    gameID = rs.getInt("new_game_id");
                } else {
                    throw new SQLException("Failed to generate new game ID");
                }
            }

            // Insert rows for each player
            // Iterates over all players in the Game instance (game.getPlayers())
            // Prepares and executes INSERT statement for each player

            // Iterates over all players in the game and extracts the  necessary info about each player for insertion to db
            try (PreparedStatement gameStmt = conn.prepareStatement(gameSql)) {
                for (Guesser player : game.getPlayers()) {
                    String playerName = player.getPlayerName();
                    int attemptsMade = game.getPlayerAttempts().get(playerName);
                    String playerLevel = player.getLevel().name();
                    // System.out.println("Inserting Player: " + playerName + ", Attempts: " + attemptsMade);

                    // Database Statement Execution
                    // corresponds to each ? placeholder
                    gameStmt.setInt(1, gameID);
                    gameStmt.setString(2, playerName);
                    gameStmt.setObject(3, attemptsMade); 
                    gameStmt.setString(4, playerLevel);
                    gameStmt.setBoolean(5, player.hasSolved(game.getSecretCode())); // True if player solved
                    gameStmt.setString(6, game.getFormattedDate());
                    gameStmt.setString(7, game.getSecretCode());
                    gameStmt.setString(8, String.join(", ", player.getGuesses()));
                    gameStmt.executeUpdate();
                }
            }
            // System.out.println("Saving game data for game ID: " + gameID);
            // System.out.println("Players: " + game.getPlayers().size());

            conn.commit(); // Commit transaction 
        }
    }
}


    // public List<Game> getLeaderboard(int topN) throws SQLException {
    //     throw new UnsupportedOperationException("Leaderboard diabled");
    // }
    
    // @Override
    // Method to get leaderboard, List<Game> 
    // sql query
    // list for leaderboard
    // make db connection
    // iterate through result set
    // print LB info
    // create new game with this data to populate from db

   
 
        



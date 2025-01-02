/* DAO.SQLiteGameDataDAO.java
Data Access Object; Handles all database interactions related to GameData,
    abstracting away the specifics of data persistence.
*/
// To add leaderboard, add method to retrieve top N players
// Indices set on fields for WHERE or ORDER BY
package DAO;
import Models.Game;
import Models.Guesser;
import DBConnectionManager.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;


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
        String createGameTableSQL = 
            "CREATE TABLE IF NOT EXISTS  game_data (" +
                "game_id INTEGER NOT NULL, " +
                "player_name TEXT NOT NULL, " +
                "rounds_to_solve INTEGER, " + // Null if not solved for a player
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
      
        String gameSql = "INSERT INTO game_data (" +
            "game_id, " +
            "player_name, " +
            "rounds_to_solve, " +
            "solved, " +
            "timestamp, " +
            "secret_code, " + 
            "guesses) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = DatabaseConnectionManager.getConnection(dbPath)) {
            conn.setAutoCommit(false); // Enable transaction for consistency
            int gameID;
        
            // Create new game_id
            try (PreparedStatement stmt = conn.prepareStatement("SELECT IFNULL (MAX(game_id), 0) + 1 AS new_game_id FROM game_data");
                ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        gameID = rs.getInt("new_game_id");
                    } else {
                        throw new SQLException("Failed to generate new game ID");
                    }
                }

                // Insert rows for each player
                try (PreparedStatement gameStmt = conn.prepareStatement(gameSql)) {
                    for (Guesser player : game.getPlayers()) {
                        gameStmt.setInt(1, gameID);
                        gameStmt.setString(2, player.getPlayerName());
                        gameStmt.setObject(3, player.hasSolved(game.getSecretCode()) ? player.getGuesses().size() : null); // null if not solved
                        gameStmt.setBoolean(4, player.hasSolved(game.getSecretCode())); // True if player solved
                        gameStmt.setString(5, game.getFormattedDate());
                        gameStmt.setString(6, game.getSecretCode());
                        gameStmt.setString(7, String.join(", ", player.getGuesses()));
                        gameStmt.executeUpdate();
                    }
                }
                conn.commit(); // Commit transaction 
        }
    }

    @Override
    public List<Game> getLeaderboard(int topN) throws SQLException {
        String leaderboardQuery = 
        "SELECT game_id, player_name, rounds_to_solve, solved, timestamp, secret_code, guesses " +
        "FROM game_data " +
        "WHERE solved = 1 " + // Solved games
        "ORDER BY rounds_to_solve ASC, timestamp ASC " + // Fewest rounds, oldest games first
        "LIMIT ?";
        
        List<Game> leaderboard = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConnection(dbPath);
            PreparedStatement stmt = conn.prepareStatement(leaderboardQuery)) {
            stmt.setInt(1, topN); // Set limit dynamically

        try (ResultSet rs = stmt.executeQuery()) {  
            // Temp map to group players by game ID
            Map<Integer, Game> gamesById = new HashMap<>();

            // Iterate through the result set
            while (rs.next()) {
                int gameID = rs.getInt("game_id");
                String playerName = rs.getString("player_name");
                int roundsToSolve = rs.getInt("rounds_to_solve");
                boolean solved = rs.getBoolean("solved");
                String timestamp = rs.getString("timestamp");
                String secretCode = rs.getString("secret_code");
                String guesses = rs.getString("guesses");

                // Create or retrieve the Game obj for this game_id
                Game game = gamesById.computeIfAbsent(gameID, id -> {
                    Game newGame = new Game(new ArrayList<>(), secretCode, this);
                    newGame.setGameID(gameID);
                    newGame.setFormattedDate(timestamp);
                    return newGame;
                });

                // Create a Guesser obj for the player
                Guesser player = new Guesser(playerName, null); // Pass null for Scanner since not needed here
                if (solved) {
                    player.getGuesses().addAll(Arrays.asList(guesses.split(", "))); // Parse guesses
                }
                game.getPlayers().add(player); // Add player to the Game obj

                // Update roundsToSolve and solved for the player
                game.setRoundsToSolve(roundsToSolve);
                game.setSolved(solved);
            }

            // Add all games to the leaderboard
            leaderboard.addAll(gamesById.values());
        }
    }
    return leaderboard;
    }
}


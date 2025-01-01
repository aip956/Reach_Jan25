/* DAO.SQLiteGameDataDAO.java
Data Access Object; Handles all database interactions related to GameData,
    abstracting away the specifics of data persistence.
*/
// To add leaderboard, add method to retrieve top N players
// Indices set on fields for WHERE or ORDER BY
package DAO;
import Models.Game;
import Models.Guesser;
// import DBConnectionManager.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


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

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            // Create game_data table
            try (PreparedStatement gameTableStmt = conn.prepareStatement(createGameTableSQL)) {
                gameTableStmt.execute(); // Execute table creation
            }

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


        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
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
        } catch (SQLException e) {
            System.err.println("Error saving game data: " + e.getMessage());
            throw e;
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

    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        PreparedStatement stmt = conn.prepareStatement(leaderboardQuery)) {
            stmt.setInt(1, topN); // Set limit dynamically

            try (ResultSet rs = stmt.executeQuery()) {
                // Temp map to group players by game ID
                Map<Integer, List<Guesser>> playersByGame = new HashMap<>();

                // Iterate through the result set
                while (rs.next()) {
                    int gameID = rs.getInt("game_id");
                    String playerName = rs.getString("player_name");

                    // Create a Guesser obj for the player
                    Guesser player = new Guesser(playerName, null); // Pass null for Scanner since not needed here
                    // Group players by game ID
                    playersByGame.computeIfAbsent(gameID, k -> new ArrayList<>()).add(player);
                }

                // Build Game objs for each game ID
                for (Map.Entry<Integer, List<Guesser>> entry : playersByGame.entrySet()) {
                    int gameID = entry.getKey();
                    List<Guesser> players = entry.getValue();

                    // Retrieve the game-specific data (one row per gameID)
                    ResultSet gameResult = stmt.executeQuery(
                        "SELECT DISTINCT rounds_to_solve, solved, timestamp, secret_code FROM game_data WHERE game_id = " + gameID
                    );

                    if (gameResult.next()) {
                        String secretCode = gameResult.getString("secret_code");
                        
                    }
                }
                    Game game = new Game(
                        null, // Players not fetched here; may extend if needed
                        rs.getString("secret_code"),
                        null); // Replace with GameDataDAO instance if needed
                    game.setGameID(rs.getInt("game_id"));
                    game.setPlayerName(rs.getString("player_name"));
                    game.setRoundsToSolve(rs.getInt("rounds_to_solve"));
                    game.setSolved(rs.getBoolean("solved"));
                    game.setFormattedDate(rs.getString("timestamp"));
                    leaderboard.add(game);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
            throw e;
        }
        return leaderboard;
    }
}


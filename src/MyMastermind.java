// MyMastermind.java

import Models.Guesser;
import Models.SecretKeeper;
import Models.Game;
import View.GameUI;
import DAO.GameDataDAO;
import DAO.SQLiteGameDataDAO;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class MyMastermind {
    // private static final Logger logger = LoggerFactory.getLogger(MyMastermind.class);
    public static void main(String[] args) {
        // db path to save
        String dbPath = System.getenv("DB_FILE");
        dbPath = (dbPath == null || dbPath.isEmpty()) ? "src/data/MM_Reach.db" : dbPath;
        System.out.println("Attempting to connect to the database...");


        // Dedicated main scanner
        Scanner scanner = new Scanner(System.in);
        GameDataDAO gameDataDAO; // Declaring outside try block

        // Initialize the database connection
        try {
            gameDataDAO = new SQLiteGameDataDAO(dbPath);
            System.out.println("DB connect success");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }


        // Check CLI args for leaderboard flag
        boolean displayLeaderboardFlag = false;
        int topN = 3; // Default display 3 players
        if (args.length > 0 && (args[0].equals("--leaderboard") || args[0].equals("--l"))) {
            displayLeaderboardFlag = true;
            if (args.length > 1) {
                try {
                    topN = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number for leaderboard; using default 3.");
                }
            }
        }

        try {
            System.out.print("Enter number of players: ");
            int numPlayers = Integer.parseInt(scanner.nextLine());
            

            List<Guesser> players = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String playerName = scanner.nextLine();
                players.add(new Guesser(playerName, scanner)); // Pass player name; shared scanner; instantiate guesser
            }
        
            System.out.println("Players added: " + players.size());

            // Flag check
            boolean debugMode = false;
            for (String arg : args) {
                if ("-d".equals(arg)) {
                    debugMode = true;
                }
            }
            System.out.println("debugger: " + debugMode);

            // Initialize secret keeper
            SecretKeeper secretKeeper = new SecretKeeper();
            String secretCode = secretKeeper.getSecretCode();

            // Start game
            Game game = new Game(players, secretCode, gameDataDAO);
            game.startGame();

            // Prompt to show lboard after game
            if (displayLeaderboardFlag) {
                displayLeaderboard(dbPath, topN);
            } 
            
        } finally {
            System.out.println("Game finished.");
            scanner.close(); // Close the scanner
            System.out.println("Scanner closed");
        }
    }
    
    // Display leaderboard
    // @param dbPath = path to SQLite db
    // @param topN = Number of top players to display
    private static void displayLeaderboard(String dbPath, int topN) {
        try {
            GameDataDAO gameDataDAO = new SQLiteGameDataDAO(dbPath);
            List<Game> leaderboard = gameDataDAO.getLeaderboard(topN);

            System.out.println("Leaderboard (Top " + topN + " Players):");
            for (Game game : leaderboard) {
                System.out.println("Player: " + game.getPlayerName() + 
                ", Rounds: " + game.getRoundsToSolve() +
                ", Solved: " + game.isSolved() +
                ", Timestamp: " + game.getFormattedDate());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
    }
}


// MyMastermind.java

import Controller.LeaderboardMngr;
import Controller.GameSetup;
import Models.Guesser;
import Models.PlayerLevel;
import Models.SecretKeeper;
import Models.Game;
import View.GameUI;
import DAO.GameDataDAO;
import DAO.SQLiteGameDataDAO;
import DBConnectionManager.DatabaseConnectionManager;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.stream.Collectors;


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

        // Determine the db type
        String dbType = System.getenv("DB_TYPE");
        dbType = (dbType == null || dbPath.isEmpty()) ? "sqlite" : dbType;

        GameDataDAO gameDataDAO; // Declaring outside try block
        // Initialize the database connection
        try {
            gameDataDAO = new SQLiteGameDataDAO(dbPath);
            System.out.println("DB connect success");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }
    
    
        // Intitialize LeaderboardMngr
        // LeaderboardMngr leaderboardMngr = new LeaderboardMngr(gameDataDAO);
        Scanner scanner = new Scanner(System.in);
        GameSetup gameSetup = new GameSetup(scanner);

        try {
            // Set up players
            List<Guesser> players = gameSetup.setupPlayers(gameDataDAO, args);

            // Generate secret code
            SecretKeeper secretKeeper = new SecretKeeper();
            String secretCode = secretKeeper.getSecretCode();

            // Initialize the game
            Game game = new Game(players, secretCode, gameDataDAO);

            // Start game
            game.startGame();

            // Handle leaderboard logic
            // leaderboardMngr.handleLeaderboard(args);

        } finally {
            System.out.println("Game finished.");
            scanner.close(); // Close the scanner
            System.out.println("Scanner closed");
            DatabaseConnectionManager.closeConnection();
        }
    }
}


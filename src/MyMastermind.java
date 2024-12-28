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
import java.util.Scanner;
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
        GameDataDAO gameDataDAO; // Declaring outside try block

        try {
            gameDataDAO = new SQLiteGameDataDAO(dbPath);
            System.out.println("DB connect success");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }

        // Shared scanner
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter number of players: ");
            int numPlayers = Integer.parseInt(scanner.nextLine());

            List<Guesser> players = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String playerName = scanner.nextLine();
                players.add(new Guesser(playerName, scanner)); // Pass player name and shared scanner; instantiate guesser
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


            SecretKeeper secretKeeper = new SecretKeeper();
            String secretCode = secretKeeper.getSecretCode();
            Game game = new Game(players, secretCode, gameDataDAO);
            // Start game
            game.startGame();
        } finally {
            scanner.close(); // Close the scanner
            System.out.println("Scanner closed");
        }
    }
}


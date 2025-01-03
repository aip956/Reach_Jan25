// Controller.GameInit.java

package Controller;

import DAO.GameDataDAO;
import Models.Guesser;
import Models.PlayerLevel;
import Models.SecretKeeper;
import Models.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class GameInit {
    private Scanner scanner;

    public GameInit(Scanner scanner) {
        this.scanner = scanner;
    }

    public Game initGame(GameDataDAO gameDataDAO, String[] args) {
        try {
            // Get num of players
            System.out.print("Enter number of players: ");
            int numPlayers = Integer.parseInt(scanner.nextLine());
            
            // Set up player names and levels
            List<Guesser> players = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String playerName = scanner.nextLine();

                // Prompt for level
                System.out.println("Select level for " + playerName + " (1 = Beginner, 2 = Medium, 3 = Hard): ");
                int levelChoice = Integer.parseInt(scanner.nextLine());
                PlayerLevel level = PlayerLevel.fromChoice(levelChoice); // Use static method
                players.add(new Guesser(playerName, level, scanner)); // Pass player name; shared scanner; instantiate guesser
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

            // Handle leaderboard logic
            leaderboardMngr.handleLeaderboard(args);

            
        }
    }
    
}

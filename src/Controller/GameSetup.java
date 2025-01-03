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



public class GameSetup {
    private Scanner scanner;

    public GameSetup(Scanner scanner) {
        this.scanner = scanner;
    }

    public List<Guesser> setupPlayers(GameDataDAO gameDataDAO, String[] args) {
        List<Guesser> players = new ArrayList<>();
        int numPlayers;

        while (true) {
            try {
                // Get num of players
                System.out.print("Enter number of players: ");
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers <= 0) {
                    System.out.println("Number of players must be > 0. Enter again.");
                    continue;
                }
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input; please enter a valid number.");
            }
        }
            
        // Set up player names and levels
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();

            // Prompt for level
            System.out.println("Select level for " + playerName + " (1 = Beginner, 2 = Medium, 3 = Hard): ");
            int levelChoice;
            while(true) { // Loop until valid level choice made
                try {
                    levelChoice = Integer.parseInt(scanner.nextLine());
                    if (levelChoice < 1 || levelChoice > 3) {
                        System.out.println("Invalid choice; select 1, 2, or 3.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input; select 1, 2, or 3.");
                }
            }
            PlayerLevel level = PlayerLevel.fromChoice(levelChoice); // Use static method
            players.add(new Guesser(playerName, level, scanner)); // Pass player name; shared scanner; instantiate guesser
        }
        System.out.println("Players added: " + players.size());
        return players;
    }
    
    // Flag check
    public boolean checkDebugMode (String[] args) {
        for (String arg : args) {
            if ("-d".equals(arg)) {
                System.out.println("Debugger enabled");
                return true;
            }
        }
        return false; // Debugger off
    }
}



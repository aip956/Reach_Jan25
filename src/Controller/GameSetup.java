// Controller.GameInit.java

package Controller;

import DAO.GameDataDAO;
import Models.Guesser;
import Models.PlayerLevel;
// import Models.SecretKeeper;
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
        int numPlayers = 1;

        // Default player
        // enter number of players, validate
            
        // Set up player names and levels
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();

            // Prompt for level
            PlayerLevel level = PlayerLevel.MEDIUM;
            // logic to select level
            // logic to validate level input
            
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



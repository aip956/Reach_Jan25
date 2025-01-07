// Controller.GameInit.java

package Controller;

import DAO.GameDataDAO;
import Models.Guesser;
import Models.PlayerLevel;
// import Models.SecretKeeper;
import Models.Game;

import Utils.ValidationUtils;

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
            // PlayerLevel level = PlayerLevel.MEDIUM;
            // prompt for leve; logic to select level
            // logic to validate level input
            
            int levelChoice;
            System.out.println("Pick a level; 1=Beginner; 2=Medium, 3=Hard");
            
            // Validation
            while(true) {
                levelChoice = Integer.parseInt(scanner.nextLine());
                // do a try/catch
                System.out.println("Level Choice: " + levelChoice);
                if(levelChoice < 1 || levelChoice > 3) {
      
                    System.out.println("Please pick 1, 2, or 3");
                    continue;
                }
                break;
            }
            


            PlayerLevel level = PlayerLevel.fromChoice(levelChoice);
            
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



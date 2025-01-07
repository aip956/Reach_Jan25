// Models.Game.java

package Models;

import Utils.ValidationUtils;
import View.GameUI;
import DAO.GameDataDAO;

import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.SQLException;




public class Game {

    private List<Guesser> players; // List of players if multiplayer
    private GameUI gameUI;
    private String secretCode;
    private List<String> guesses;
    private boolean solved;
    private GameDataDAO gameDataDAO;
    private Map<String, Integer> playerAttempts = new HashMap<>();
    private String formattedDate;
    private int gameID; 
    



    // class constructor; use List<Guesser> players for guesser
    public Game (List<Guesser> players, String secretCode, GameDataDAO gameDataDAO) {
        this.players = players;
        this.secretCode = secretCode;
        this.gameUI = new GameUI();
        this.guesses = new ArrayList<>();
        this.gameDataDAO = gameDataDAO;      
        this.solved = false;
        this.playerAttempts = new HashMap<>();

        // Initialize all players' attempts
        for (Guesser player : players) {
            playerAttempts.put(player.getPlayerName(), 0);
        }
    }
    

    // Getters, setters; moved
    public int getGameID() {
        return gameID;
    }
    public void setGameID (int gameID) {
        this.gameID = gameID;
        // logger.debug("48GameIDD: {}", gameID);
    }

    public String getFormattedDate() {
        return formattedDate;
    }
    public void setFormattedDate (String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public List<Guesser> getPlayers() {
        return players;
    }

    public Map<String, Integer> getPlayerAttempts() {
        return playerAttempts;
    }


    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean hasAttemptsLeft(Guesser player) {
        return playerAttempts.get(player.getPlayerName()) < player.getLevel().getMaxAttempts();
    }

    public String getSecretCode() {
        return secretCode;
    }


    // Method to check if guess is four nums 0 - 8
    public boolean isValidGuess(String guess) {
        // 4 digits 0 - 7
        return guess != null && guess.matches(ValidationUtils.VALID_GUESS_PATTERN);
    }




    public void startGame() {
        // Use gameUI for UI interactions
        gameUI.displayMessage("Will you find the secret code?\nGood luck!");

        while (!solved && players.stream().anyMatch(this::hasAttemptsLeft)) {
            Guesser currentPlayer = players.get(0);
            String playerName = currentPlayer.getPlayerName();
            PlayerLevel level = currentPlayer.getLevel();

            // Display current player's status
            gameUI.displayMessage("Current player: " + playerName);
            gameUI.displayMessage("Attempts left: " + (level.getMaxAttempts() - playerAttempts.get(playerName)));

            // Make a guess
            String guess;
            while (true) { // Loop until a valid guess entered
                guess = currentPlayer.makeGuess();
                gameUI.displayMessage("Player " + playerName + "'s guess: " + guess);
                
                if (isValidGuess(guess)) {
                    break; // Exit the loop if guess valid
                } else {
                    gameUI.displayMessage("Invalid input! Try again with numbers 0 - 7.");
                }
            }
            guesses.add(guess);

            // Process valid guess
            playerAttempts.put(playerName, playerAttempts.get(playerName) + 1);

            // Check if guess is correct
            if (guess.equals(secretCode)) {
                gameUI.displayMessage("Congrats " + playerName + "! You did it");
                solved = true;
            } else {
                String feedback = provideFeedback(currentPlayer, guess);
                gameUI.displayMessage(feedback);
            }

            // Move to next player
            players.add(players.remove(0)); // Rotate player list
        }

        if (!solved) {
            gameUI.displayMessage("Sorry, the code was: " + secretCode);
        }
        finalizeGameData();
    }

    public String provideFeedback(Player player, String guess) {
        if (!player.getLevel().isShowDetailedFb()) {
            return "Guess evaluated. No detailed feedback for you.";
        }

        
        int wellPlaced = 0;
        int misPlaced = 0;
        Map<Character, Integer> secretCount = new HashMap<>();
        // Map<Character, Integer> guessCount = new HashMap<>();

        // Count well placed; populate hash
        for (int i = 0; i < secretCode.length(); i++) {
            if (secretCode.charAt(i) == guess.charAt(i)) {
                wellPlaced++;
            } else {
                secretCount.put(secretCode.charAt(i), secretCount.getOrDefault(secretCode.charAt(i), 0) + 1);
            }
        }

        // Count mis-placed
        for (int i = 0; i < guess.length(); i++) {
            // if at i, secret != guess, and guess(i) in secretCount
            // incr misPl, decr secrCount
            if (secretCode.charAt(i) != guess.charAt(i) && secretCount.getOrDefault(guess.charAt(i), 0) > 0) {
                misPlaced++;
                secretCount.put(guess.charAt(i), secretCount.get(guess.charAt(i)) - 1);
            }
        }
        return String.format("Well placed pieces: %d\nMisplaced pieces: %d", wellPlaced, misPlaced);
    }




    private void finalizeGameData() {
        formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        gameUI.displayMessage("Finalizing game data...");

        try {
            // Save game data
            gameDataDAO.saveGameData(this);
            // gameUI.displayMessage("Game data saved");
        } catch (SQLException e) {
            gameUI.displayMessage("Error occured saving game data: " + e.getMessage());
        } finally {
            gameUI.close(); 
        }              
    }
}



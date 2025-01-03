// Models.Game.java

package Models;

import Utils.ValidationUtils;
import View.GameUI;
import DAO.GameDataDAO;

import java.util.*;
import java.text.SimpleDateFormat;
// import java.util.Date;
import java.sql.SQLException;
// import java.util.stream.Collectors;



public class Game {

    private List<Guesser> players; // List of players if multiplayer
    // private int currentPlayerIndex; // Track the player
    private GameUI gameUI;
    private String secretCode;
    private List<String> guesses;
    // public int attemptsLeft;
    private boolean solved;
    private GameDataDAO gameDataDAO;
    private Map<String, Integer> playerAttempts = new HashMap<>();
    private String formattedDate;

    // public static final int MAX_ATTEMPTS = 5;
    private int gameID; 
    



    // class constructor; use List<Guesser> players for guesser
    public Game (List<Guesser> players, String secretCode, GameDataDAO gameDataDAO) {
        this.players = players;
        // this.currentPlayerIndex = 0; // Start with the first player
        this.secretCode = secretCode;
        this.gameUI = new GameUI();
        this.guesses = new ArrayList<>();
        // this.attemptsLeft = MAX_ATTEMPTS;
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

    // public void setPlayerAttempts(Map<String, Integer> playerAttempts) {
    //     this.playerAttempts = playerAttempts;
    // }


    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean hasAttemptsLeft(Guesser player) {
        return playerAttempts.get(player.getPlayerName()) < player.getLevel().getMaxAttempts();
    }

    // Method to check if guess is four nums 0 - 8
    public boolean isValidGuess(String guess) {
        // 4 digits 0 - 7
        return guess != null && guess.matches(ValidationUtils.VALID_GUESS_PATTERN);
    }




    public String getSecretCode() {
        return secretCode;
    }

    // public List<String> getGuesses() {
    //     return guesses;
    // }
    // public void setGuesses(List<String> guesses) {
    //     this.guesses = guesses;
    // }

    // public static int getMaxAttempts() {
    //     return MAX_ATTEMPTS;
    // }
    // public int getAttemptsLeft() {
    //     return attemptsLeft;
    // }




    // public void evaluateGuess(String guess) {
    //     if (!isValidGuess(guess)) {
    //         throw new IllegalArgumentException("Invalid guess");
    //     }
    //     guesses.add(guess); // Add guess to list of guesses
    //     attemptsLeft--;
    // }



    

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
            // String guess = currentPlayer.makeGuess();
            String guess;
            while (true) { // Loop until a valid guess entered
                guess = currentPlayer.makeGuess();
                gameUI.displayMessage("Player " + playerName + "'s guess: " + guess);
                
                if (isValidGuess(guess)) {
                    break; // Exit the loop if guess valid
                } else {
                    gameUI.displayMessage("Invalid input! Try again.");
                }
            }
            guesses.add(guess);

            // Process valid guess
            // evaluateGuess(guess);
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

        // if (!isValidGuess(guess)) {
        //     return "Invalid guess; enter 4 digits, 0 - 7.";
        // }
        
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
            // char secretChar = secretCode.charAt(i);
            // char guessChar = guess.charAt(i);

            // if (secretChar == guessChar) {
            //     wellPlaced++;
            // }
            // else {
            //     secretCount.put(secretChar, secretCount.getOrDefault(secretChar, 0) + 1);
            //     guessCount.put(guessChar, guessCount.getOrDefault(guessChar, 0) + 1);
            // }
        }

        // Count mis-placed
        for (int i = 0; i < guess.length(); i++) {
            if (secretCode.charAt(i) != guess.charAt(i) && secretCount.getOrDefault(guess.charAt(i), 0) > 0) {
                misPlaced++;
                secretCount.put(guess.charAt(i), secretCount.get(guess.charAt(i)) - 1);
            }
        }
        // for (char c : guessCount.keySet()) {
        //     if (secretCount.containsKey(c)) {
        //         misPlaced += Math.min(secretCount.get(c), guessCount.get(c));
        //     }
        // }
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



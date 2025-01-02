// Models.Game.java

package Models;

import Utils.ValidationUtils;
import View.GameUI;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;
import java.util.stream.Collectors;
import DAO.GameDataDAO;


public class Game {
    // Fields
    // To make multiplayer, replace single Guesser with List<Guesser> to 
    // store multiplayers
    // add logic to track the current player taking a turn; int currentPlayerIndex
    // modify method startGame to loop through players
    private List<Guesser> players; // List of players if multiplayer
    private int currentPlayerIndex; // Track the player
    // comment out the Guesser guesser
    // private Guesser guesser;
    private GameUI gameUI;
    private String secretCode;
    private List<String> guesses;
    public int attemptsLeft;
    private boolean solved;
    private GameDataDAO gameDataDAO;
    

    public static final int MAX_ATTEMPTS = 5;

    private int gameID; 
    // private String playerName;
    private int roundsToSolve;
    private String formattedDate;


    // class constructor; use List<Guesser> players for guesser
    public Game (List<Guesser> players, String secretCode, GameDataDAO gameDataDAO) {
        this.players = players;
        this.currentPlayerIndex = 0; // Start with the first player
        this.secretCode = secretCode;
        this.gameUI = new GameUI();
        this.guesses = new ArrayList<>();
        this.attemptsLeft = MAX_ATTEMPTS;
        this.gameDataDAO = gameDataDAO;      
        this.solved = false;
    }
    
    // // Constructor for leaderboard 
    // public Game(String playerName, String secretCode, int roundsToSolve, boolean solved, String formattedDate) {
    //     this.playerName = playerName;
    //     this.secretCode = secretCode;
    //     this.roundsToSolve = roundsToSolve;
    //     this.solved = solved;
    //     this.formattedDate = formattedDate;
    // }

    // Getters, setters; moved
    public int getGameID() {
        return gameID;
    }
    public void setGameID (int gameID) {
        this.gameID = gameID;
        // logger.debug("48GameIDD: {}", gameID);
    }

    // public String getPlayerName() {
    //     return playerName;
    // }
    // public void setPlayerName(String playerName) {
    //     this.playerName = playerName;
    //     // logger.debug("57playerName: {}", playerName);
    // }

    public List<Guesser> getPlayers() {
        return players;
    }

    public int getRoundsToSolve() {
        return roundsToSolve;
    }
    public void setRoundsToSolve(int roundsToSolve) {
        this.roundsToSolve = roundsToSolve;
        // logger.debug("66roundsToSolve: {}", roundsToSolve);
    }

    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
        // logger.debug("75solved: {}", solved);
    }

    public String getFormattedDate() {
        return formattedDate;
    }
    public void setFormattedDate (String formattedDate) {
        this.formattedDate = formattedDate;
    }

    // Do I need this getter?
    public String getSecretCode() {
        return secretCode;
    }


    public List<String> getGuesses() {
        return guesses;
    }
    public void setGuesses(List<String> guesses) {
        this.guesses = guesses;
        // logger.debug("102guesses: {}", guesses);
    }

    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }
    public int getAttemptsLeft() {
        return attemptsLeft;
    }
    public boolean hasAttemptsLeft() {
        return attemptsLeft > 0;
    }

    // Method to check if guess is four nums 0 - 8
    public boolean isValidGuess(String guess) {
        // 4 digits 0 - 7
        return guess != null && guess.matches(ValidationUtils.VALID_GUESS_PATTERN);
    }

    public void evaluateGuess(String guess) {
        if (!isValidGuess(guess)) {
            throw new IllegalArgumentException("Invalid guess");
        }
        guesses.add(guess); // Add guess to list of guesses
        attemptsLeft--;
    }



    public String provideFeedback(String guess) {

        if (!isValidGuess(guess)) {
            return "Invalid guess; enter 4 digits, 0 - 7.";
        }
        
        int wellPlaced = 0;
        int misPlaced = 0;
        Map<Character, Integer> secretCount = new HashMap<>();
        Map<Character, Integer> guessCount = new HashMap<>();

        // Count well placed; populate hash
        for (int i = 0; i < 4; i++) {
            char secretChar = secretCode.charAt(i);
            char guessChar = guess.charAt(i);

            if (secretChar == guessChar) {
                wellPlaced++;
            }
            else {
                secretCount.put(secretChar, secretCount.getOrDefault(secretChar, 0) + 1);
                guessCount.put(guessChar, guessCount.getOrDefault(guessChar, 0) + 1);
            }
        }

        // Count mis-placed
        for (char c : guessCount.keySet()) {
            if (secretCount.containsKey(c)) {
                misPlaced += Math.min(secretCount.get(c), guessCount.get(c));
            }
        }
        return String.format("Well placed pieces: %d\nMisplaced pieces: %d", wellPlaced, misPlaced);
    }

    public void startGame() {
        // Use gameUI for UI interactions
        gameUI.displayMessage("Will you find the secret code?\nGood luck!");

        while (hasAttemptsLeft()) {
            Guesser currentPlayer = players.get(currentPlayerIndex);
            gameUI.displayMessage("Current player: " + currentPlayer.getPlayerName());

            String guess = currentPlayer.makeGuess();
            gameUI.displayMessage("Player " + currentPlayer.getPlayerName() + "'s guess: " + guess);

            if (isValidGuess(guess)) {
                evaluateGuess(guess);
                String feedback = provideFeedback(guess);
                gameUI.displayMessage(feedback);

                if (guess.equals(secretCode)) {
                    gameUI.displayMessage("Congrats " + currentPlayer.getPlayerName() + "! You did it");
                    solved = true;
                    break;
                }
            } else {
                gameUI.displayMessage("Invalid input! Try again.");
            }

            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        if (!solved) {
            gameUI.displayMessage("Sorry, the code was: " + secretCode);
        }
            
        finalizeGameData();
    }

    private void finalizeGameData() {
        // Build summary of all players
        String playerSummary = players.stream()
            .map(Player::getPlayerName) // Get each player's name
            .collect(Collectors.joining(", ")); // Combine names with a comma

        // for (Guesser player : players) {
        //     playerSummary.append(player.getPlayerName()).append(", ");
        // }

        // // Remove trailing comma and space
        // if (playerSummary.length() > 2) {
        //     playerSummary.setLength(playerSummary.length() - 2);
        // }

        // this.playerName = playerSummary.toString(); // combine names for game summary
        
        // Compute game-related details
        int roundsToSolve = MAX_ATTEMPTS - attemptsLeft;
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.formattedDate = formattedDate;


        try {
            // Save game data and get the generated game ID
            gameDataDAO.saveGameData(this);

            // Save player data for each player
            // for (Guesser player : players) {
            //     gameDataDAO.savePlayerData(gameID, player.getPlayerName(), guesses);
            // }
            gameUI.displayMessage("Game data saved");
        } catch (SQLException e) {
            gameUI.displayMessage("Error occured saving game data: " + e.getMessage());
        } finally {
            gameUI.close(); 
        }              
    }
}



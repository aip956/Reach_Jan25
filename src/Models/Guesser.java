// Models.Guesser.java

package Models;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Guesser extends Player {
    private Scanner scanner;
    private List<String> guesses;

    public Guesser(String name, Scanner scanner) {
        super(name);// Call the Player constructor
        this.scanner = scanner; // Using shared Scanner
        this.guesses = new ArrayList<>();
    }

    @Override
    public String makeGuess() {
        System.out.print("Enter guess: ");
        String guess = scanner.nextLine();
        guesses.add(guess);
        return guess;
    }

    // Getter for guesses
    public List<String> getGuesses() {
        return guesses;
    }

    // Check if player has solved the game; null if not
    public boolean hasSolved(String secretCode) {
        return guesses.contains(secretCode);
    }
}


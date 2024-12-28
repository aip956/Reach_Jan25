// Models.Player.java

package Models;

public abstract class Player {
    protected String playerName;

    public Player(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    // Abstract method for making guesses (implemented by subclasses)
    public abstract String makeGuess();
}

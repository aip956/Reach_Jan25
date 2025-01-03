// Models.Player.java

package Models;

public abstract class Player {
    protected String playerName;
    protected PlayerLevel level;

    public Player(String name, PlayerLevel level) {
        this.playerName = name;
        this.level = level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerLevel getLevel() {
        return level;
    }

    // Abstract method for making guesses (implemented by subclasses)
    public abstract String makeGuess();
}

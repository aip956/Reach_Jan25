package Models;

public enum PlayerLevel {
    BEGINNER(15, true), // More attempts, detailed feedback
    MEDIUM(10, true),
    HARD(5, false);

    private final int maxAttempts;
    private final boolean showDetailedFb;

    PlayerLevel(int maxAttempts, boolean showDetailedFb) {
        this.maxAttempts = maxAttempts;
        this.showDetailedFb = showDetailedFb;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public boolean showDetailedFb() {
        return showDetailedFb;
    }
}

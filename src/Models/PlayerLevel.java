// Models.PlayerLevel.java

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
    
    public boolean isShowDetailedFb() {
        return showDetailedFb;
    }

    // Static method to get PlayerLevel by numeric choice
    public static PlayerLevel fromChoice(int choice) {
        switch (choice) {
            case 1:
                return BEGINNER;
            case 2:
                return MEDIUM;
            case 3:
                return HARD;
            default:
                return MEDIUM;
        }
    }
}

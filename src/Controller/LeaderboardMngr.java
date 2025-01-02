// Controller.LeaderboardMngr.java

package Controller;

import DAO.GameDataDAO;
import Models.Game;
import java.sql.SQLException;
import java.util.List;


public class LeaderboardMngr {
    private final GameDataDAO gameDataDAO;

    public LeaderboardMngr(GameDataDAO gameDataDAO) {
        this.gameDataDAO = gameDataDAO;
    }

    public void handleLeaderboard(String[] args) {
        boolean displayLeaderboardFlag = false;
        int topN = 3; // Default display 3 players

        // Check CLI args for leaderboard flag
        if (args.length > 0 && (args[0].equals("--leaderboard") || args[0].equals("--l"))) {
            displayLeaderboardFlag = true;
            if (args.length > 1) {
                try {
                    topN = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number for leaderboard; using default 3.");
                }
            }
        }
        // Prompt to show lboard after game
        if (displayLeaderboardFlag) {
            displayLeaderboard(topN);
        }
    }


    private void displayLeaderboard(int topN) {
        try {
            List<Game> leaderboard = gameDataDAO.getLeaderboard(topN);

            System.out.println("Leaderboard (Top " + topN + " Players):");
                for (Game game : leaderboard) {
                for (String playerName : game.getPlayerAttempts().keySet()) {
                    int attemptsMade = game.getPlayerAttempts().get(playerName);
                    System.out.println("Player: " + playerName +
                    ", Attempts: " + attemptsMade +
                    ", Solved: " + game.isSolved() +
                    ", Timestamp: " + game.getFormattedDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
    }
}

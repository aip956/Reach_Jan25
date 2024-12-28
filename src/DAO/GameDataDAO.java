// GameDataDAO.java

package DAO;
import Models.Game;

import java.sql.SQLException;
import java.util.List;


public interface GameDataDAO {
    // Data Access Object (DAO) interface; allows games to be pulled by playerName and solved
    void saveGameData(Game game) throws SQLException; // Save a game's data to db
    // Placehold for getTopGames list

}


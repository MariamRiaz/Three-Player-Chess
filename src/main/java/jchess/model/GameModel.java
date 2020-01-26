/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.model;

import jchess.entities.Player;
import jchess.helper.Images;

import java.io.Serializable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Class representings game gameModel available for the current player
 */
public class GameModel implements IGameModel {

    private static ResourceBundle loc = null;
    private int timeForGame;
    private boolean timeLimitSet;
    private Player activePlayer;
    private boolean blockedChessboard;

    public enum gameModes {

        newGame
    }

    private gameModes gameMode;
    private Player playerWhite;
    private Player playerBlack;
    private Player playerGray;

    public void setTimeForGame(int timeForGame) {
        this.timeForGame = timeForGame;
    }

    public boolean getTimeLimitSet(){
        return timeLimitSet;
    }

    public void setTimeLimitSet(boolean limitSet){
        this.timeLimitSet = limitSet;
    }

    public void setGameMode(gameModes gameMode) {
        this.gameMode = gameMode;
    }

    public Player getPlayerWhite() {
        return playerWhite;
    }

    public Player getPlayerBlack() {
        return playerBlack;
    }

    public Player getPlayerGray() {
        return playerGray;
    }

    public void setBlockedChessboard(boolean blockedChessboard) {
        this.blockedChessboard = blockedChessboard;
    }

    public boolean isBlockedChessboard() {
        return blockedChessboard;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public GameModel() {
        // temporally
        this.playerWhite = new Player("", Images.WHITE_COLOR);
        this.playerBlack = new Player("", Images.BLACK_COLOR);
        this.playerGray = new Player("", Images.GREY_COLOR);
        this.timeLimitSet = false;
        gameMode = gameModes.newGame;
    }

    // TODO : put somewhere else
    public static String getTexts(String key) {
        if (GameModel.loc == null) {
            GameModel.loc = PropertyResourceBundle.getBundle("i18n.main");
        }
        String result;
        try {
            result = GameModel.loc.getString(key);
        } catch (java.util.MissingResourceException exc) {
            result = key;
        }
        return result;
    }

    /**
     * Method to get game time set by player
     *
     * @return timeFofGame int with how long the game will leasts
     */
    public int getTimeForGame() {
        return this.timeForGame;
    }
}

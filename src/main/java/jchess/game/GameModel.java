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
package jchess.game;

import jchess.game.player.Player;
import jchess.io.Images;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Class representings game gameModel available for the current player
 */
public class GameModel implements IGameModel {

    private int timeForGame;
    private boolean timeLimitSet;
    private Player activePlayer;
    private boolean blockedChessboard;

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
        this.playerWhite = new Player("", Images.WHITE_COLOR);
        this.playerBlack = new Player("", Images.BLACK_COLOR);
        this.playerGray = new Player("", Images.GREY_COLOR);
        this.timeLimitSet = false;
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

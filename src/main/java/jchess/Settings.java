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
package jchess;

import jchess.entities.Player;
import jchess.helper.Images;

import java.io.Serializable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Class representings game settings available for the current player
 */
public class Settings implements Serializable {

    private static ResourceBundle loc = null;
    public int timeForGame;
    public boolean runningChat;
    public boolean runningGameClock;
    public boolean timeLimitSet;// tel us if player choose time 4 game or it's infinity
    public boolean upsideDown;

    public enum gameModes {

        newGame, loadGame
    }

    public gameModes gameMode;
    private Player playerWhite;
    private Player playerBlack;
    private Player playerGray;

    public Player getPlayerWhite() {
        return playerWhite;
    }

    public void setPlayerWhite(Player playerWhite) {
        this.playerWhite = playerWhite;
    }

    public Player getPlayerBlack() {
        return playerBlack;
    }

    public void setPlayerBlack(Player playerBlack) {
        this.playerBlack = playerBlack;
    }

    public Player getPlayerGray() {
        return playerGray;
    }

    public void setPlayerGray(Player playerGray) {
        this.playerGray = playerGray;
    }


    public enum gameTypes {

        local, network
    }

    public gameTypes gameType;
    public boolean renderLabels = true;

    public Settings() {
        // temporally
        this.playerWhite = new Player("", Images.WHITE_COLOR);
        this.playerBlack = new Player("", Images.BLACK_COLOR);
        this.playerGray = new Player("", Images.GREY_COLOR);
        this.timeLimitSet = false;
        gameMode = gameModes.newGame;
    }

    public static String lang(String key) {
        if (Settings.loc == null) {
            Settings.loc = PropertyResourceBundle.getBundle("i18n.main");
            Locale.setDefault(Locale.ENGLISH);
        }
        String result = "";
        try {
            result = Settings.loc.getString(key);
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

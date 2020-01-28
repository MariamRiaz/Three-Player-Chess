package jchess.game;

import jchess.game.player.Player;

import java.io.Serializable;

/**
 * Interface a GameModel must implement.
 */
public interface IGameModel extends Serializable {

    /**
     * getter for the white Player.
     *
     * @return white Player
     */
    Player getPlayerWhite();

    /**
     * getter for the black Player.
     *
     * @return black Player
     */
    Player getPlayerBlack();

    /**
     * getter for the gray Player.
     *
     * @return gray Player
     */
    Player getPlayerGray();

    /**
     * setter to set if the Chessboard is blocked as a Game Tab is switched.
     *
     * @param blockedChessboard true if Chessboard will be blocked
     */
    void setBlockedChessboard(boolean blockedChessboard);

    /**
     * getter to check if Chessboard is blocked.
     *
     * @return true if Chessboard is blocked
     */
    boolean isBlockedChessboard();

    /**
     * setter for the active Player.
     *
     * @param activePlayer Player to set active
     */
    void setActivePlayer(Player activePlayer);

    /**
     * getter for the active Player.
     *
     * @return active Player
     */
    Player getActivePlayer();

    /**
     * getter for Game Time.
     *
     * @return Game Time.
     */
    int getTimeForGame();

    /**
     * setter for Game Time.
     *
     * @param timeForGame Time
     */
    void setTimeForGame(int timeForGame);

    /**
     * getter to check if Time Limit is set for this Game.
     *
     * @return true if Time Limit is set
     */
    boolean getTimeLimitSet();

    /**
     * setter for Time Limit.
     *
     * @param limitSet true sets the Time Limit
     */
    void setTimeLimitSet(boolean limitSet);

}

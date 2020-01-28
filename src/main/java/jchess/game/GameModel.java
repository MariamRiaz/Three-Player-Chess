package jchess.game;

import jchess.game.player.Player;
import jchess.io.Images;

/**
 * Class representings game gameModel available for the current player.
 */
public class GameModel implements IGameModel {

    private int timeForGame;
    private boolean timeLimitSet;
    private Player activePlayer;
    private boolean blockedChessboard;

    private Player playerWhite;
    private Player playerBlack;
    private Player playerGray;

    /**
     * {@inheritDoc}
     */
    public void setTimeForGame(int timeForGame) {
        this.timeForGame = timeForGame;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getTimeLimitSet() {
        return timeLimitSet;
    }

    /**
     * {@inheritDoc}
     */
    public void setTimeLimitSet(boolean limitSet) {
        this.timeLimitSet = limitSet;
    }

    /**
     * {@inheritDoc}
     */
    public Player getPlayerWhite() {
        return playerWhite;
    }

    /**
     * {@inheritDoc}
     */
    public Player getPlayerBlack() {
        return playerBlack;
    }

    /**
     * {@inheritDoc}
     */
    public Player getPlayerGray() {
        return playerGray;
    }

    /**
     * {@inheritDoc}
     */
    public void setBlockedChessboard(boolean blockedChessboard) {
        this.blockedChessboard = blockedChessboard;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBlockedChessboard() {
        return blockedChessboard;
    }

    /**
     * {@inheritDoc}
     */
    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    /**
     * {@inheritDoc}
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * Constructor for GameModel Class.
     */
    public GameModel() {
        this.playerWhite = new Player("", Images.WHITE_COLOR);
        this.playerBlack = new Player("", Images.BLACK_COLOR);
        this.playerGray = new Player("", Images.GREY_COLOR);
        this.timeLimitSet = false;
    }

    /**
     * {@inheritDoc}
     */
    public int getTimeForGame() {
        return this.timeForGame;
    }
}

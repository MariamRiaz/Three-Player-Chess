package jchess.model;

import jchess.entities.Player;

import java.io.Serializable;

public interface IGameModel extends Serializable {

    Player getPlayerWhite();

    Player getPlayerBlack();

    Player getPlayerGray();

    void setBlockedChessboard(boolean blockedChessboard);

    boolean isBlockedChessboard();

    void setActivePlayer(Player activePlayer);

    Player getActivePlayer();

    int getTimeForGame();

    void setGameMode(GameModel.gameModes gameMode);

    void setTimeForGame(int timeForGame);

    boolean getTimeLimitSet();

    void setTimeLimitSet(boolean limitSet);

}

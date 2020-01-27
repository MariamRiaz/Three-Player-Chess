package jchess.game;

import jchess.game.clock.IGameClock;

import java.util.Observer;

public interface IGameController extends Observer {

    IGameModel getGameModel();

    IGameClock getGameClock();

    AbstractGameView getView();

    void endGame(String message);

    boolean undo();

    boolean redo();

    boolean rewindToBegin();

    boolean rewindToEnd();

}

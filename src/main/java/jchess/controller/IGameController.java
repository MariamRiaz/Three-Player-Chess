package jchess.controller;

import jchess.model.GameModel;
import jchess.view.GameView;

import java.util.Observer;

public interface IGameController extends Observer {

    GameModel getGameModel();

    GameClock getGameClock();

    GameView getView();

    void endGame(String message);

    boolean undo();

    boolean redo();

    boolean rewindToBegin();

    boolean rewindToEnd();

}

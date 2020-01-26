package jchess.controller;

import jchess.model.IGameModel;
import jchess.view.AbstractGameView;

import java.util.Observer;

public interface IGameController extends Observer {

    IGameModel getGameModel();

    GameClock getGameClock();

    AbstractGameView getView();

    void endGame(String message);

    boolean undo();

    boolean redo();

    boolean rewindToBegin();

    boolean rewindToEnd();

}

package jchess.game;

import jchess.game.clock.IGameClock;

import java.util.Observer;

/**
 * Interface that a GameController must implement.
 */
public interface IGameController extends Observer {

    /**
     * getter for Model of the Game Component.
     *
     * @return GameModel
     */
    IGameModel getGameModel();

    /**
     * getter for the Game Clock Component.
     *
     * @return GameClock
     */
    IGameClock getGameClock();

    /**
     * getter for the View of the Game Component.
     *
     * @return View of the Game Component
     */
    AbstractGameView getView();

    /**
     * Method to end the Game and show Message on Game End.
     *
     * @param message Message
     */
    void endGame(String message);


    /**
     * Checks if the current state can be undone.
     *
     * @return True if the current state of the game is undoable
     */
    boolean undo();

    /**
     * Checks if a move can be redone from the current state of the game (undo an undo).
     *
     * @return True if the most recent undone move can be redone
     */
    boolean redo();

    /**
     * Undoes all Moves to the Begin.
     *
     * @return Returns true if the current game can be rewound to the begin
     */
    boolean rewindToBegin();

    /**
     * Undoes all Moves that were undone.
     *
     * @return Returns true if the current game can be rewound to the end
     */
    boolean rewindToEnd();

}

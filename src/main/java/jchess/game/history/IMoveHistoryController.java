package jchess.game.history;

import jchess.move.effects.BoardTransition;

import java.util.List;
import java.util.Queue;
import javax.swing.JScrollPane;

/**
 * Interface that a MoveHistoryController must implement.
 */
public interface IMoveHistoryController {
    /**
     * Adds a board transtion to the move history table
     * @param boardTransition The transition to be added
     */
    void addMove(BoardTransition boardTransition);

    /**
     * method to clear the MoveForwardStack (for undo)
     */
    void clearMoveForwardStack();

    /**
     * getter for the ScrollPane of the MoveHistoryView
     *
     * @return JScrollPane of MoveHistoryView
     */
    JScrollPane getScrollPane();

    /**
     * getter for the List of Moves as String
     *
     * @return List of Moves
     */
    List<String> getMoves();

    /**
     * gets as Queue of MoveEffects that were undone
     *
     * @return Queue of undone MoveEffects
     */
    Queue<BoardTransition> undo();

    /**
     * gets the Move that was undone
     *
     * @return Move that was undone
     */
    BoardTransition undoOne();

    /**
     * gets a Queue of MoveEffects that were redone
     *
     * @return Queue of MoveEffects that were redone
     */
    Queue<BoardTransition> redo();

    /**
     * gets a single MoveEffect that was redone
     *
     * @return MoveEffect that was redone
     */
    BoardTransition redoOne();

    /**
     * switches Columns according to the active Player
     *
     * @param forward boolean for switch Column direction (false for undoing a move)
     */
    void switchColumns(boolean forward);
}

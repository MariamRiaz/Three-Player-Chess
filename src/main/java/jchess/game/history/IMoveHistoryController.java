package jchess.game.history;

import jchess.move.effects.MoveEffect;

import java.util.List;
import java.util.Queue;
import javax.swing.JScrollPane;

/**
 * Interface that a MoveHistoryController must implement.
 */
public interface IMoveHistoryController {

    /**
     * adds MoveEffect to the MoveHistory Table
     *
     * @param moveEffects       MoveEffect to add to the Table
     * @param registerInHistory boolean whether MoveEffect will be added to the MoveHistory
     * @param registerInTable   boolean whether MoveEffect will be added to the MoveHistory Table
     */
    void addMove(MoveEffect moveEffects, boolean registerInHistory, boolean registerInTable);

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
    Queue<MoveEffect> undo();

    /**
     * gets the Move that was undone
     *
     * @return Move that was undone
     */
    MoveEffect undoOne();

    /**
     * gets a Queue of MoveEffects that were redone
     *
     * @return Queue of MoveEffects that were redone
     */
    Queue<MoveEffect> redo();

    /**
     * gets a single MoveEffect that was redone
     *
     * @return MoveEffect that was redone
     */
    MoveEffect redoOne();

    /**
     * switches Columns according to the active Player
     *
     * @param forward boolean for switch Column direction (false for undoing a move)
     */
    void switchColumns(boolean forward);
}

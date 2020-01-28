package jchess.game.history;

import jchess.move.effects.BoardTransition;

import java.util.List;
import java.util.Stack;
import javax.swing.table.DefaultTableModel;

/**
 * Abstract Class a MoveHistoryModel must extend.
 */
public abstract class AbstractMoveHistoryModel extends DefaultTableModel {

    /**
     * getter for the activePlayerColumn of the MoveHistory Table
     *
     * @return activePlayerColumn
     */
    public abstract MoveHistoryController.PlayerColumn getActivePlayerColumn();

    /**
     * getter for the MoveBackStack
     *
     * @return MoveBackStack
     */
    public abstract Stack<BoardTransition> getMoveBackStack();

    /**
     * getter for the MoveForwardStack
     *
     * @return MoveForwardStack
     */
    public abstract Stack<BoardTransition> getMoveForwardStack();

    /**
     * getter for the List Moves contained in the MoveHistory Table as Strings
     *
     * @return Moves
     */
    public abstract List<String> getMove();

    /**
     * setter for the activePlayerColumn
     *
     * @param activePlayerColumn activePlayerColumn to set it to
     */
    public abstract void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn);

    /**
     * getter for the currentRow of the MoveHistory Table
     *
     * @return currentRow
     */
    public abstract int getCurrentRow();

    /**
     * setter for the currentRow of the MoveHistory Table
     *
     * @param currentRow Row to set the currentRow to
     */
    public abstract void setCurrentRow(int currentRow);
}

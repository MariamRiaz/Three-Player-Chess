package jchess.game.history;

import jchess.move.effects.MoveEffect;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Class that represents the Model of the MoveHistoryComponent.
 */
public class MoveHistoryModel extends AbstractMoveHistoryModel {

    private Stack<MoveEffect> moveBackStack;
    private Stack<MoveEffect> moveForwardStack;
    private MoveHistoryController.PlayerColumn activePlayerColumn = MoveHistoryController.PlayerColumn.player1;
    private ArrayList<String> move;
    private int currentRow;

    /**
     * Constructor for MoveHistoryModel
     */
    public MoveHistoryModel() {
        super();
        this.moveBackStack = new Stack<>();
        this.moveForwardStack = new Stack<>();
        this.move = new ArrayList<>();
        this.addTableModelListener(null);
    }

    /**
     * {@inheritDoc}
     */
    public Stack<MoveEffect> getMoveBackStack() {
        return moveBackStack;
    }

    /**
     * {@inheritDoc}
     */
    public Stack<MoveEffect> getMoveForwardStack() {
        return moveForwardStack;
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList<String> getMove() {
        return move;
    }

    /**
     * {@inheritDoc}
     */
    public MoveHistoryController.PlayerColumn getActivePlayerColumn() {
        return activePlayerColumn;
    }

    /**
     * {@inheritDoc}
     */
    public void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn) {
        this.activePlayerColumn = activePlayerColumn;
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    /**
     * returns if a table cell is editable
     *
     * @param a row of the cell
     * @param b column of the cell
     * @return if cell is editable
     */
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}

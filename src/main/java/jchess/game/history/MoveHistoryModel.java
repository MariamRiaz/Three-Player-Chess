package jchess.game.history;

import jchess.move.effects.MoveEffect;

import java.util.ArrayList;
import java.util.Stack;

public class MoveHistoryModel extends AbstractMoveHistoryModel {


    private Stack<MoveEffect> moveBackStack;
    private Stack<MoveEffect> moveForwardStack;
    private MoveHistoryController.PlayerColumn activePlayerColumn = MoveHistoryController.PlayerColumn.player1;
    private ArrayList<String> move;
    private int currentRow;


    public MoveHistoryModel() {
        super();
        this.moveBackStack = new Stack<>();
        this.moveForwardStack = new Stack<>();
        this.move = new ArrayList<>();
        this.addTableModelListener(null);
    }

    public Stack<MoveEffect> getMoveBackStack() {
        return moveBackStack;
    }

    public Stack<MoveEffect> getMoveForwardStack() {
        return moveForwardStack;
    }

    public ArrayList<String> getMove() {
        return move;
    }

    public MoveHistoryController.PlayerColumn getActivePlayerColumn() {
        return activePlayerColumn;
    }

    public void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn) {
        this.activePlayerColumn = activePlayerColumn;
    }

    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    @Override
    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}

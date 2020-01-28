package jchess.game.history;

import jchess.move.effects.BoardTransition;

import java.util.ArrayList;
import java.util.Stack;

public class MoveHistoryModel extends AbstractMoveHistoryModel {


    private Stack<BoardTransition> moveBackStack;
    private Stack<BoardTransition> moveForwardStack;
    private MoveHistoryController.PlayerColumn activePlayerColumn = MoveHistoryController.PlayerColumn.player1;
    public int rowsNum = 0;
    private ArrayList<String> move;


    public MoveHistoryModel() {
        super();
        this.moveBackStack = new Stack<>();
        this.moveForwardStack = new Stack<>();
        this.move = new ArrayList<>();
        this.addTableModelListener(null);
    }

    public Stack<BoardTransition> getMoveBackStack() {
        return moveBackStack;
    }

    public Stack<BoardTransition> getMoveForwardStack() {
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
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}

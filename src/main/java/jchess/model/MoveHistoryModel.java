package jchess.model;

import jchess.controller.MoveHistoryController;
import jchess.move.effects.MoveEffect;

import java.util.ArrayList;
import java.util.Stack;

public class MoveHistoryModel extends AbstractMoveHistoryModel{


    public Stack<MoveEffect> moveBackStack = new Stack<>();
    public Stack<MoveEffect> moveForwardStack = new Stack<>();
    private MoveHistoryController.PlayerColumn activePlayerColumn = MoveHistoryController.PlayerColumn.player1;
    public int rowsNum = 0;
    public ArrayList<String> move = new ArrayList<>();


    public MoveHistoryModel() {
        super();
        this.addTableModelListener(null);
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

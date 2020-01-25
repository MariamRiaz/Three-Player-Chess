package jchess.model;

import jchess.controller.MoveHistoryController;
import jchess.move.effects.MoveEffect;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Stack;

public class MoveHistoryModel extends DefaultTableModel {


    public Stack<MoveEffect> moveBackStack = new Stack<>();
    public Stack<MoveEffect> moveForwardStack = new Stack<>();
    public MoveHistoryController.PlayerColumn activePlayerColumn = MoveHistoryController.PlayerColumn.player1;
    public int rowsNum = 0;
    public ArrayList<String> move = new ArrayList<>();


    public MoveHistoryModel() {
        super();
        this.addTableModelListener(null);
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}

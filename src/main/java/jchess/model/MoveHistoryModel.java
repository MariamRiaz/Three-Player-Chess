package jchess.model;

import jchess.controller.MoveHistoryController;
import jchess.move.effects.MoveEffect;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.EmptyStackException;
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


    public MoveEffect undo() {
        MoveEffect last = null;
        try {
            last = this.moveBackStack.pop();
        } catch (EmptyStackException | ArrayIndexOutOfBoundsException exc) {
            exc.printStackTrace();
        }

        if (last != null) {
            this.moveForwardStack.push(last);

            if (activePlayerColumn.equals(MoveHistoryController.PlayerColumn.player1)) {
                if (this.getRowCount() > 0)
                    this.setValueAt("", this.getRowCount() - 1, 2);

            } else if (activePlayerColumn.equals(MoveHistoryController.PlayerColumn.player2)) {
                this.setValueAt("", this.getRowCount() - 1, 0);
                this.removeRow(this.getRowCount() - 1);
                if (this.rowsNum > 0)
                    this.rowsNum--;

            } else {
                if (this.getRowCount() > 0)
                    this.setValueAt("", this.getRowCount() - 1, 1);
            }

            this.move.remove(this.move.size() - 1);
        }
        return last;
    }

    public MoveEffect redo() {
        try {
            MoveEffect first = this.moveForwardStack.pop();
            this.moveBackStack.push(first);

            return first;
        } catch (java.util.EmptyStackException exc) {
            return null;
        }
    }
}

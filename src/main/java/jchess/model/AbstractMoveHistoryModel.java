package jchess.model;

import jchess.controller.MoveHistoryController;
import jchess.move.effects.MoveEffect;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Stack;

public abstract class AbstractMoveHistoryModel extends DefaultTableModel {

    public int rowsNum;
    public Stack<MoveEffect> moveBackStack;
    public Stack<MoveEffect> moveForwardStack;
    public ArrayList<String> move;

    public abstract MoveHistoryController.PlayerColumn getActivePlayerColumn();

    public abstract void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn);
}

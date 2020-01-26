package jchess.model;

import jchess.controller.MoveHistoryController;
import jchess.move.effects.MoveEffect;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Stack;

public abstract class AbstractMoveHistoryModel extends DefaultTableModel {

    public int rowsNum;

    public abstract MoveHistoryController.PlayerColumn getActivePlayerColumn();

    public abstract Stack<MoveEffect> getMoveBackStack();

    public abstract Stack<MoveEffect> getMoveForwardStack();

    public abstract ArrayList<String> getMove();

    public abstract void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn);
}

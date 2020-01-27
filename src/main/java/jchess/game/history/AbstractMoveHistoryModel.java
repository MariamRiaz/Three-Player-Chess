package jchess.game.history;

import jchess.move.effects.MoveEffect;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Stack;

public abstract class AbstractMoveHistoryModel extends DefaultTableModel {

    public int rowsNum;

    public abstract MoveHistoryController.PlayerColumn getActivePlayerColumn();

    public abstract Stack<MoveEffect> getMoveBackStack();

    public abstract Stack<MoveEffect> getMoveForwardStack();

    public abstract List<String> getMove();

    public abstract void setActivePlayerColumn(MoveHistoryController.PlayerColumn activePlayerColumn);
}

package jchessTest;

import jchess.game.history.MoveHistoryModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class MoveHistoryModelTest {
    MoveHistoryModel model = new MoveHistoryModel();

    @Test
    public void addColumn() {
        model.addColumn("FirstPlayer");
        assertEquals(1, model.getColumnCount());
    }

    @Test
    public void addRow() {
        model.addRow(new String[2]);
        assertEquals(1, model.getRowCount());
    }

    @Test
    public void setValueAt() {
        model.addColumn("FirstPlayer");
        model.addColumn("SecondPlayer");
        model.addRow(new String[2]);
        model.setValueAt("a2-a3",0,1);
        assertEquals("a2-a3", model.getValueAt(0,1));
    }

    @Test
    public void getRowCount() {
        assertEquals(0, model.getRowCount());
    }

    @Test
    public void removeRow() {
        model.addRow(new String[2]);
        model.addRow(new String[2]);
        model.removeRow(1);
        assertEquals(1, model.getRowCount());
    }
}
package jchessTest;

import jchess.view.MoveHistoryView;
import org.junit.Test;

import static org.junit.Assert.*;

public class MoveHistoryViewTest {
    MoveHistoryView moveHistoryView = new MoveHistoryView();

    @Test
    public void addColumn() {
        moveHistoryView.addColumn("FirstPlayer");
        assertEquals(1, moveHistoryView.getColumnCount());
    }

    @Test
    public void addRow() {
        moveHistoryView.addRow();
        assertEquals(1, moveHistoryView.getRowCount());
    }

    @Test
    public void setValueAt() {
        moveHistoryView.addColumn("FirstPlayer");
        moveHistoryView.addColumn("SecondPlayer");
        moveHistoryView.addRow();
        moveHistoryView.setValueAt("a2-a3",0,1);
        assertEquals("a2-a3", moveHistoryView.getValueAt(0,1));
    }

    @Test
    public void getRowCount() {
        assertEquals(0, moveHistoryView.getRowCount());
    }

    @Test
    public void removeRow() {
        moveHistoryView.addRow();
        moveHistoryView.addRow();
        moveHistoryView.removeRow(1);
        assertEquals(1, moveHistoryView.getRowCount());
    }
}
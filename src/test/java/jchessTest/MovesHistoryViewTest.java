package jchessTest;

import jchess.view.MovesHistoryView;
import org.junit.Test;

import static org.junit.Assert.*;

public class MovesHistoryViewTest {
    MovesHistoryView movesHistoryView = new MovesHistoryView();

    @Test
    public void addColumn() {
        movesHistoryView.addColumn("FirstPlayer");
        assertEquals(1, movesHistoryView.getColumnCount());
    }

    @Test
    public void addRow() {
        movesHistoryView.addRow();
        assertEquals(1, movesHistoryView.getRowCount());
    }

    @Test
    public void setValueAt() {
        movesHistoryView.addColumn("FirstPlayer");
        movesHistoryView.addColumn("SecondPlayer");
        movesHistoryView.addRow();
        movesHistoryView.setValueAt("a2-a3",0,1);
        assertEquals("a2-a3", movesHistoryView.getValueAt(0,1));
    }

    @Test
    public void getRowCount() {
        assertEquals(0, movesHistoryView.getRowCount());
    }

    @Test
    public void removeRow() {
        movesHistoryView.addRow();
        movesHistoryView.addRow();
        movesHistoryView.removeRow(1);
        assertEquals(1, movesHistoryView.getRowCount());
    }
}
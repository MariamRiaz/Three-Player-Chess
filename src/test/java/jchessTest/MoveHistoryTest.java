package jchessTest;

import org.junit.Assert;
import org.junit.Test;
import jchess.Game;
import jchess.Settings;
import jchess.pieces.MoveHistory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryTest {
    Game game;
    MoveHistory moveHistory;
    @Before
    public void setup() {
        game = mock (Game.class);
        moveHistory = new MoveHistory(game);
    }
    @Test
    public void getValueAt() {
/*        when(moveHistory.getValueAt(1,1).thenReturn("a2-a3"));
        assertEquals("a2-a3", moveHistory.getValueAt(1,1));*/
    }
    @Test
    public void getRowCount() { assertEquals(0, moveHistory.getRowCount());
    }
    @Test
    public void getColumnCount() { assertEquals(3, moveHistory.getColumnCount());
    }
    @Test
    public void getMoves() {
        assert moveHistory.getMoves().isEmpty();
    }
    @Test
    public void addMoves() {
        moveHistory.addMove("a2-a3");
        moveHistory.addMove("b7-b6");
        assertEquals(0, moveHistory.getRowCount());
    }
}
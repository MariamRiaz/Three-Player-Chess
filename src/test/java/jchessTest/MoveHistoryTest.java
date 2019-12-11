package jchessTest;

import jchess.Game;
import jchess.pieces.MoveHistory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class MoveHistoryTest {
    Game game;
    MoveHistory moveHistory;
    @Before
    public void setup() {
        game = mock (Game.class);
        moveHistory = new MoveHistory(game);
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
}
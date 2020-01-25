package jchessTest;

import jchess.Game;
import jchess.controller.MoveHistoryController;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.pieces.Piece;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryControllerTest {
    Game game;
    MoveHistoryController moveHistoryController;
    private Settings settingsMock;
    Square square1;
    Square square2;
    private Piece pieceMock;
    ArrayList<Character> columnNames = new ArrayList<>();

    @Before
    public void setup() {
        game = mock (Game.class);
        settingsMock = mock(Settings.class);
        pieceMock = mock(Piece.class);
        Player testPlayer = new Player("test", "white");
        columnNames.add('a');
        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);
        moveHistoryController = new MoveHistoryController(columnNames);
    }
    @Test
    public void getMoves() {
        assert moveHistoryController.getMoves().isEmpty();
    }

}
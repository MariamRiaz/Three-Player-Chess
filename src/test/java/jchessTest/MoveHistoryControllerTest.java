package jchessTest;

import jchess.game.GameController;
import jchess.game.IGameController;
import jchess.game.history.MoveHistoryController;
import jchess.game.player.Player;
import jchess.game.chessboard.model.Square;
import jchess.game.GameModel;
import jchess.pieces.Piece;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryControllerTest {
    IGameController gameController;
    MoveHistoryController moveHistoryController;
    private GameModel settingsMock;
    Square square1;
    Square square2;
    private Piece pieceMock;
    ArrayList<Character> columnNames = new ArrayList<>();

    @Before
    public void setup() {
        gameController = mock(GameController.class);
        settingsMock = mock(GameModel.class);
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
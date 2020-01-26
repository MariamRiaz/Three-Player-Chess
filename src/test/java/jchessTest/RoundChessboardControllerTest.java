package jchessTest;

import jchess.model.GameModel;
import jchess.controller.MoveHistoryController;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.controller.RoundChessboardController;
import jchess.model.RoundChessboardModel;
import jchess.move.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceLoader;
import jchess.view.RoundChessboardView;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardControllerTest {


    private GameModel settingsMock;
    private MoveHistoryController moveHistoryControllerMock;
    private RoundChessboardController controller;
    private RoundChessboardModel modelMock;
    private RoundChessboardView viewMock;
    private Square square;

    @Before
    public void beforeEachTest() {
        Player testPlayer = new Player("test", "white");
        settingsMock = mock(GameModel.class);
        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);

        moveHistoryControllerMock = mock(MoveHistoryController.class);
        modelMock = mock(RoundChessboardModel.class);
        viewMock = mock(RoundChessboardView.class);
        square = new Square(10, 10, new Piece(PieceLoader.getPieceDefinition("King"), settingsMock.getPlayerWhite(), new Orientation()));

        controller = new RoundChessboardController(modelMock, viewMock, settingsMock, moveHistoryControllerMock);
    }

    @Test
    public void testUnselect() {
        controller.setActiveSquare(square);
        controller.unselect();
        assertNull(controller.getActiveSquare());

    }

    @Test
    public void testSetActiveSquare() {
        controller.setActiveSquare(square);
        assertEquals(square, controller.getActiveSquare());
    }
}

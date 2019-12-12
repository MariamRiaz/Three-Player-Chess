package jchessTest;

import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.controller.RoundChessboardController;
import jchess.model.RoundChessboardModel;
import jchess.controller.MoveHistory;
import jchess.pieces.PieceFactory;
import jchess.view.RoundChessboardView;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardControllerTest {


    private Settings settingsMock;
    private MoveHistory moveHistoryMock;
    private RoundChessboardController controller;
    private RoundChessboardModel modelMock;
    private RoundChessboardView viewMock;
    private Square square;

    @Before
    public void beforeEachTest() {
        Player testPlayer = new Player("test", "white");
        settingsMock = mock(Settings.class);
        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);

        moveHistoryMock = mock(MoveHistory.class);
        modelMock = mock(RoundChessboardModel.class);
        viewMock = mock(RoundChessboardView.class);
        square = new Square(10, 10, PieceFactory.createKing(settingsMock.getPlayerWhite()));

        controller = new RoundChessboardController(modelMock, viewMock, settingsMock, moveHistoryMock);
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

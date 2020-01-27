package jchessTest;

import jchess.game.history.MoveHistoryController;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.player.Player;
import jchess.game.chessboard.model.Square;
import jchess.game.GameModel;
import jchess.game.IGameModel;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.move.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceLoader;
import jchess.game.chessboard.view.RoundChessboardView;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardControllerTest {


    private IGameModel gameModel;
    private MoveHistoryController moveHistoryControllerMock;
    private RoundChessboardController controller;
    private RoundChessboardModel modelMock;
    private RoundChessboardView viewMock;
    private Square square;
    private RoundChessboardLoader chessboardLoaderMock;

    @Before
    public void beforeEachTest() {
        Player testPlayer = new Player("test", "white");
        gameModel = mock(GameModel.class);
        when(gameModel.getPlayerWhite()).thenReturn(testPlayer);
        square = new Square(10, 10, new Piece(PieceLoader.getPieceDefinition("King"), gameModel.getPlayerWhite(), new Orientation()));
        ArrayList squareList = new ArrayList();
        squareList.add(square);


        modelMock = mock(RoundChessboardModel.class);
        when(modelMock.getColumns()).thenReturn(2);
        when(modelMock.getRows()).thenReturn(2);
        when(modelMock.getSquares()).thenReturn(squareList);

        viewMock = mock(RoundChessboardView.class);
        moveHistoryControllerMock = mock(MoveHistoryController.class);

        chessboardLoaderMock = mock(RoundChessboardLoader.class);
        when(chessboardLoaderMock.loadDefaultFromJSON(gameModel)).thenReturn(modelMock);


        controller = new RoundChessboardController(chessboardLoaderMock, 800, gameModel, moveHistoryControllerMock);
    }

//    @Test
//    public void testUnselect() {
//        controller.setActiveSquare(square);
//        controller.unselect();
//        assertNull(controller.getActiveSquare());
//
//    }
//
//    @Test
//    public void testSetActiveSquare() {
//        controller.setActiveSquare(square);
//        assertEquals(square, controller.getActiveSquare());
//    }
}

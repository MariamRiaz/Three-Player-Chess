package jchessTest.chessboard;

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
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardControllerTest {


    private RoundChessboardController sut;

    @Mock
    private MoveHistoryController moveHistoryControllerMock;
    @Mock
    private RoundChessboardModel modelMock;

    private List<Square> createSquares() {
        List<Square> squares = new ArrayList<>();
        squares.add(new Square(0, 0, null));
        squares.add(new Square(1, 0, null));
        squares.add(new Square(0, 1, null));
        squares.add(new Square(1, 1, null));
        return squares;
    }

    @Before
    public void setup() {
        modelMock = mock(RoundChessboardModel.class);
        moveHistoryControllerMock = mock(MoveHistoryController.class);
        when(modelMock.getColumns()).thenReturn(2);
        when(modelMock.getRows()).thenReturn(2);
        when(modelMock.getSquares()).thenReturn(createSquares());
        sut = new RoundChessboardController(modelMock, 100, moveHistoryControllerMock);
//        Player testPlayer = new Player("test", "white");
//        gameModel = mock(GameModel.class);
//        when(gameModel.getPlayerWhite()).thenReturn(testPlayer);
//        square = new Square(10, 10, new Piece(PieceLoader.getPieceDefinition("King"), gameModel.getPlayerWhite(), new Orientation()));
//        ArrayList squareList = new ArrayList();
//        squareList.add(square);
//
//
//        modelMock = mock(RoundChessboardModel.class);
//        when(modelMock.getColumns()).thenReturn(2);
//        when(modelMock.getRows()).thenReturn(2);
//        when(modelMock.getSquares()).thenReturn(squareList);
//
//        viewMock = mock(RoundChessboardView.class);
//        moveHistoryControllerMock = mock(MoveHistoryController.class);
//
//        chessboardLoaderMock = mock(RoundChessboardLoader.class);
//        when(chessboardLoaderMock.loadDefaultFromJSON(gameModel)).thenReturn(modelMock);
    }

    @Test
    public void testInit() {
        assertNotNull(sut.getModel());
        assertNotNull(sut.getView());
    }

    @Test
    public void testSelectSquare() {
        // Arrange
        Square square = mock(Square.class);
        // Act
        sut.select(square);
        // Assert
        assertEquals(sut.getActiveSquare(), square);
    }
//
//    @Test
//    public void testSetActiveSquare() {
//        controller.setActiveSquare(square);
//        assertEquals(square, controller.getActiveSquare());
//    }
}

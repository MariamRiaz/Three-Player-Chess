package jchessTest.chessboard;

import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryController;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.player.Player;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.io.Images;
import jchess.move.MoveEvaluator;
import jchess.pieces.Piece;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardControllerTest {

    RoundChessboardController sut;
    RoundChessboardLoader chessboardLoader;
    IMoveHistoryController moveHistoryController;
    MoveEvaluator evaluator;
    RoundChessboardModel model;

    private void setupModel(RoundChessboardModel model) {
        List<Square> squares = new ArrayList<>();
        squares.add(new Square(0, 0, null));
        squares.add(new Square(1, 0, null));
        squares.add(new Square(0, 1, null));
        squares.add(new Square(1, 1, null));
        when(model.getColumns()).thenReturn(2);
        when(model.getRows()).thenReturn(2);
        when(model.getSquares()).thenReturn(squares);
    }

    @Before
    public void setUp() {
        chessboardLoader = new RoundChessboardLoader();
        evaluator = mock(MoveEvaluator.class);
        model = mock(RoundChessboardModel.class);
        setupModel(model);
        moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());
        sut = new RoundChessboardController(model, 800, moveHistoryController);
    }

    @Test
    public void testInit() {
        assertNotNull(sut.getModel());
        assertNotNull(sut.getView());
        assertNotNull(sut.getSquares());
    }

    @Test
    public void getView() {
        AbstractChessboardView view = sut.getView();
        assert view.getWidth() == 800;
        assert view.getHeight() == 800;
        assert view.getCircleCenter().getX() == 400;
        assert view.getCircleCenter().getY() == 400;
    }

    @Test
    public void testMoveIsNotPossible() {
        // Arrange
        Piece piece = mock(Piece.class);
        when(evaluator.getPieceTargetToSavePieces(piece, new HashSet<>())).thenReturn(new HashSet<>());
        Square squareFrom = new Square(0, 0, piece);
        Square squareTo = new Square(1, 1, piece);
        when(model.getSquare(squareTo.getPozX(), squareTo.getPozY())).thenReturn(squareTo);
        // Assert
        assertFalse(sut.moveIsPossible(squareFrom, squareTo, evaluator));
    }

    @Test
    public void pieceIsUnsavable() {
        assertEquals(true, sut.pieceIsUnsavable(mock(Piece.class)));
    }

//    @Test
//    public void getCrucialPieces() {
//        HashSet<Piece> crucialPieces = sut.getCrucialPieces(new Player("", Images.WHITE_COLOR));
//        Piece piece = (Piece) crucialPieces.toArray()[0];
//        assert piece.getDefinition().getType().equals("King");
//    }

//    @Test
//    public void getActiveSquare() {
//        roundChessboardController.setActiveSquare(null);
//        //roundChessboardController.select(new Square(1, 1, mock(Piece.class)));
//        System.out.println(roundChessboardController.getActiveSquare());
//    }

//    @Test
//    public void getSquareTest1() {
//        assert sut.getSquare(1, 1).equals(new Square(1, 1, null));
//        //assert roundChessboardController.getSquare(1000, 1000).equals(new Square(1000, 1000, null));
//    }

//    @Test
//    public void getSquareTest3() {
//        HashSet<Piece> crucialPieces = sut.getCrucialPieces(new Player("", Images.WHITE_COLOR));
//        Piece piece = (Piece) crucialPieces.toArray()[0];
//        assert sut.getSquare(piece).getPozX() == 5;
//        assert sut.getSquare(piece).getPozY() == 3;
//    }

    @Test (expected = NullPointerException.class)
    public void getSquareTest2() {
        sut.getSquare(1000, 1000).equals(new Square(1000, 1000, null));
    }

//    @Test
//    public void getSquares() {
//        assert sut.getSquares().size() == 144;
//
//    }

    @Test
    public void undo() {
        assert sut.undo() == false;
    }

    @Test
    public void redo() {
        assert sut.redo() == false;
    }

//    @Test
//    public void getSquareFromClick() {
//        assert sut.getSquareFromClick(200, 100).equals(new Square(5, 15, null));
//        assert sut.getSquareFromClick(100, 200).equals(new Square(5, 14, null));
//    }

    @Test
    public void getModel() {
//        RoundChessboardModel model = chessboardLoader.loadDefaultFromJSON(gameModel);
//        System.out.println(roundChessboardController.getModel());
    }
}

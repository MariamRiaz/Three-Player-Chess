package jchess.game.chessboard.controller;

import jchess.game.GameModel;
import jchess.game.IGameModel;
import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.chessboard.view.RoundChessboardView;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryController;
import jchess.game.player.Player;
import jchess.io.Images;
import jchess.pieces.Piece;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RoundChessboardControllerTest {

    RoundChessboardController roundChessboardController;
    RoundChessboardLoader chessboardLoader;
    GameModel gameModel;
    IMoveHistoryController moveHistoryController;

    @Before
    public void setUp() throws Exception {
        chessboardLoader = new RoundChessboardLoader();
        gameModel = new GameModel();
        gameModel.setBlockedChessboard(false);
        gameModel.setActivePlayer(gameModel.getPlayerWhite());
        moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());
        roundChessboardController = new RoundChessboardController(chessboardLoader, 800, gameModel, moveHistoryController);
    }

    @Test
    public void getView() {
        AbstractChessboardView view = roundChessboardController.getView();
        assert view.getWidth() == 800;
        assert view.getHeight() == 800;
        assert view.getCircleCenter().getX() == 400;
        assert view.getCircleCenter().getY() == 400;
    }

    @Test
    public void moveIsPossible() {
        Piece piece = mock(Piece.class);
        Square squareFrom = new Square(1, 1, piece);
        Square squareTo = new Square(5, 5, piece);
        assertEquals(false, roundChessboardController.moveIsPossible(squareFrom, squareTo));
    }

    @Test
    public void testMoveIsPossible() {
        assertEquals(false, roundChessboardController.moveIsPossible(1, 1, 5, 5));
    }

    @Test
    public void pieceIsUnsavable() {
        assertEquals(true, roundChessboardController.pieceIsUnsavable(mock(Piece.class)));
    }

    @Test
    public void getCrucialPieces() {
        HashSet<Piece> crucialPieces = roundChessboardController.getCrucialPieces(new Player("", Images.WHITE_COLOR));
        Piece piece = (Piece) crucialPieces.toArray()[0];
        assert piece.getDefinition().getType().equals("King");
    }

    @Test
    public void getActiveSquare() {
        roundChessboardController.select(new Square(1, 1, mock(Piece.class)));
        System.out.println(roundChessboardController.getActiveSquare());
    }

    @Test
    public void getSquareTest1() {
        assert roundChessboardController.getSquare(1, 1).equals(new Square(1, 1, null));
        //assert roundChessboardController.getSquare(1000, 1000).equals(new Square(1000, 1000, null));
    }

    @Test (expected = NullPointerException.class)
    public void getSquareTest2() {
        assert roundChessboardController.getSquare(1000, 1000).equals(new Square(1000, 1000, null));
    }

    @Test
    public void getSquares() {
    }

    @Test
    public void undo() {
    }

    @Test
    public void redo() {
    }

    @Test
    public void getSquareFromClick() {
    }

    @Test
    public void testGetSquare() {
    }

    @Test
    public void getModel() {
    }
}
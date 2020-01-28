package jchess.game.chessboard.model;

import jchess.game.GameModel;
import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryController;
import jchess.game.player.Player;
import jchess.io.Images;
import jchess.pieces.Piece;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class RoundChessboardModelTest {
    RoundChessboardModel roundChessboardModel;
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
        //roundChessboardModel = new RoundChessboardModel(5, 20, false, false);
        roundChessboardModel = roundChessboardController.getModel();
    }

    @Test
    public void getRows() {
        assert roundChessboardModel.getRows() == 24;
    }

    @Test
    public void getColumns() {
        assert roundChessboardModel.getColumns() == 6;
    }

    @Test
    public void getCrucialPieces() {
        HashSet<Piece> crucialPieces = roundChessboardModel.getCrucialPieces(new Player("", Images.WHITE_COLOR));
        Piece piece = (Piece) crucialPieces.toArray()[0];
        assert piece.getDefinition().getType().equals("King");
    }

    @Test
    public void getSquareTest1() {
        assert roundChessboardModel.getSquare(1).equals(new Square(5, 0, null));
        assert roundChessboardModel.getSquare(4).equals(new Square(5, 3, null));
    }

    @Test
    public void isInPromotionArea() {
        assert roundChessboardModel.isInPromotionArea(new Square(5, 0, null)) == true;
        assert roundChessboardModel.isInPromotionArea(new Square(0, 5, null)) == false;
    }

    @Test
    public void getInnerRimConnected() {
        assert roundChessboardModel.getInnerRimConnected() == true;
        RoundChessboardModel roundChessboardModel2 = new RoundChessboardModel(5, 20, false, false);
        assert roundChessboardModel2.getInnerRimConnected() == false;
    }

    @Test
    public void getSquareTest2() {
        HashSet<Piece> crucialPieces = roundChessboardModel.getCrucialPieces(new Player("", Images.WHITE_COLOR));
        Piece piece = (Piece) crucialPieces.toArray()[0];
        System.out.println(roundChessboardModel.getSquare(piece).equals(new Square(5, 3, piece)));
    }

    @Test
    public void getSquareTest3() {
        assert roundChessboardModel.getSquare(5, 20).equals(new Square(5, 20, null));
    }

    @Test
    public void getSquaresBetween() {
        Square fromSquare = new Square(5, 0, null);
        Square toSquare = new Square(5, 5, null);
        assert roundChessboardModel.getSquaresBetween(fromSquare, toSquare).size() == 6;
    }

    @Test
    public void setPieceOnSquare() {
        HashSet<Piece> crucialPieces = roundChessboardModel.getCrucialPieces(new Player("", Images.WHITE_COLOR));
        Piece piece = (Piece) crucialPieces.toArray()[0];
        System.out.println(piece.getDefinition().getType());
        roundChessboardModel.setPieceOnSquare(piece, new Square(3, 4, piece));
        System.out.println(roundChessboardModel.getSquare(3, 4).getPiece().getDefinition().getType());
    }

    @Test
    public void getSquares() {
        assert roundChessboardModel.getSquares().size() == 144;
    }
}
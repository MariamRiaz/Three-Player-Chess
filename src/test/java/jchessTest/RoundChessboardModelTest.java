package jchessTest;

import jchess.game.GameModel;
import jchess.game.IGameModel;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.player.Player;
import jchess.move.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardModelTest {
    RoundChessboardModel roundChessboardModel = new RoundChessboardModel(5, 20, false, false);
    Piece piece;
    //private List<Square> squares = new square1;

    @Before
    public void setup() {
        piece = mock(Piece.class);
    }

    @Test
    public void getSquare()  {
        assertEquals(null, roundChessboardModel.getSquare(piece));
        Player testPlayer = new Player("test", "white");
        piece = new Piece(PieceLoader.getPieceDefinition("King"), testPlayer, new Orientation());
        assertEquals(null, roundChessboardModel.getSquare(piece));
    }

    @Test
    public void getSquaresBetween() {
        assert roundChessboardModel.getSquaresBetween(null, null).isEmpty();

        roundChessboardModel.getSquaresBetween(new Square(1, 1, null), new Square(1, 5, null));
        assert roundChessboardModel.getSquaresBetween(null, null).isEmpty();
    }

    //@Test
   // public void setPieceOnSquare(Piece, Square) {
  //  }

}
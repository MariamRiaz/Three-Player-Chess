package jchessTest;

import jchess.Game;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.controller.MoveHistory;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryTest {
    Game game;
    MoveHistory moveHistory;
    private Settings settingsMock;
    Square square1;
    Square square2;
    private Piece pieceMock;

    @Before
    public void setup() {
        game = mock (Game.class);
        settingsMock = mock(Settings.class);
        pieceMock = mock(Piece.class);
        Player testPlayer = new Player("test", "white");
        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);
        moveHistory = new MoveHistory(game);
    }
    @Test
    public void getMoves() {
        assert moveHistory.getMoves().isEmpty();
    }
    @Test
    public void addMove() {
        Piece piece = PieceFactory.createKing(settingsMock.getPlayerWhite());
        square1 = new Square(10, 10, piece);
        square2 = new Square(11, 11, null);
        Piece pieceClone = piece.clone();
        this.moveHistory.addMove(square1, square2, piece, pieceClone,
                null, false, MoveHistory.castling.none,
                false, null);
        assert moveHistory.getMoves().size() != 0;
    }
}
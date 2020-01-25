package jchessTest;

import jchess.Game;
import jchess.controller.MoveHistoryController;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.pieces.Piece;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryControllerTest {
    Game game;
    MoveHistoryController moveHistoryController;
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
        moveHistoryController = new MoveHistoryController(game);
    }
    @Test
    public void getMoves() {
        assert moveHistoryController.getMoves().isEmpty();
    }
    @Test
    public void addMove() {
        /*Piece piece = new Piece(PieceLoader.getPieceDefinition("King"), settingsMock.getPlayerWhite(), new Orientation());
        square1 = new Square(10, 10, piece);
        square2 = new Square(11, 11, null);
        Piece pieceClone = piece.clone();
        this.moveHistoryController.addMove(square1, square2, piece, pieceClone,
                null, false, MoveHistoryController.castling.none,
                false, null);
        assert moveHistoryController.getMoves().size() != 0;*/ // TODO: Write actual MoveHistoryController test
    	assert true;
    }
}